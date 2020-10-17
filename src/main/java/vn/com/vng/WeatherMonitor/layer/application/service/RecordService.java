package vn.com.vng.WeatherMonitor.layer.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.vng.WeatherMonitor.layer.application.dao.RecordDao;
import vn.com.vng.WeatherMonitor.layer.application.dao.RegionDao;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.QuerySearch;
import vn.com.vng.WeatherMonitor.layer.application.model.RecordRespondPayload;
import vn.com.vng.WeatherMonitor.layer.application.validator.QuerySearchValidator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class RecordService {
    @Autowired
    RecordDao recordDao;

    @Autowired
    RegionDao regionDao;

    @Autowired
    ExecutorService executor;

    public List<RecordRespondPayload> listRecord(QuerySearch filter) throws InvalidDataException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InterruptedException, ExecutionException, JsonProcessingException {
        QuerySearchValidator.validate(filter);
        //region
        if(filter.getRegionCode() != null) {
            Region region = regionDao.getRegionByCode(filter.getRegionCode());
            if(region == null) {
                throw new InvalidDataException("Region not exist with code: " + filter.getRegionCode());
            }
            filter.setRegion(region);
            return recordDao.listRecordByFilter(filter).stream().map(RecordRespondPayload::new).
                    sorted((o1,o2) -> (int) (o1.getTimestamp() - o2.getTimestamp())).
                    collect(Collectors.toList());
        }
        //area
        Area area = regionDao.getAreaByCode(filter.getAreaCode());
        if(area == null) {
            throw new InvalidDataException("Area not exist with code: " + filter.getAreaCode());
        }

        List<Record> recordList = new ArrayList<>();
        List<Region> listRegions = regionDao.getAllRegionsByArea(area.getId());
        List<Callable<List<Record>>> jobList = new ArrayList<>();
        for(Region region : listRegions) {
            jobList.add(() -> {
                try {
                    QuerySearch subFilter = new QuerySearch();
                    PropertyUtils.copyProperties(subFilter, filter);
                    subFilter.setRegion(region);
                    List<Record> records = recordDao.listRecordByFilter(subFilter);
                    return records;
                } catch (Exception e){
                    e.printStackTrace();
                    return new ArrayList<>();
                }

            });
        }
        List<Future<List<Record>>> futures = executor.invokeAll(jobList);
        for(Future<List<Record>> f : futures) {
            recordList.addAll(f.get());
        }

        Map<Long, List<Record>> recordPerCheckPoint = recordList.stream().collect(Collectors.groupingBy(record -> record.getCheckPoint().getTimestamp()));

        return recordPerCheckPoint.entrySet().stream().map(entry -> {
            RecordRespondPayload recordRespondPayload = new RecordRespondPayload();
            List<Record> records = entry.getValue();
            long score = records.stream().mapToLong(r -> r.getScore()).reduce(0, (a, b) -> a + b);
            recordRespondPayload.setScore(score);
            recordRespondPayload.setRegionCode(area.getCode());
            recordRespondPayload.setTimestamp(entry.getKey());

            return recordRespondPayload;
        }).sorted((o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp())).collect(Collectors.toList());
    }
}
