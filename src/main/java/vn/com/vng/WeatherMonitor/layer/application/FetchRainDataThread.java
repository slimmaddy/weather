package vn.com.vng.WeatherMonitor.layer.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import vn.com.vng.WeatherMonitor.layer.application.dao.CheckPointDao;
import vn.com.vng.WeatherMonitor.layer.application.dao.RainViewerDao;
import vn.com.vng.WeatherMonitor.layer.application.dao.RecordDao;
import vn.com.vng.WeatherMonitor.layer.application.dao.RegionDao;
import vn.com.vng.WeatherMonitor.layer.application.entity.CheckPoint;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.RainViewerParam;
import vn.com.vng.WeatherMonitor.ultility.Util;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class FetchRainDataThread extends Thread{

    private ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();

    @Autowired
    RainViewerDao rainViewerDao;

    @Autowired
    CheckPointDao checkPointDao;

    @Autowired
    RegionDao regionDao;

    @Autowired
    ThreadPoolTaskExecutor executor;

    @Autowired
    RecordDao recordDao;

    @PostConstruct
    public void init() throws Exception {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                System.out.println("Checking for new checkpoints......................");
                CheckPoint lastCheckPoint = checkPointDao.getNewestCheckpoint();
                if(lastCheckPoint == null) {
                    System.out.println("Insert first checkpoint");
                    List<Region> regionList = regionDao.listRegions();
                    if(regionList == null || regionList.size() == 0) {
                        return;
                    }
                    Long newestCheckpoint = rainViewerDao.getNewCheckPoint();
                    CheckPoint firstCheckpoint = new CheckPoint(newestCheckpoint);
                    firstCheckpoint.setId(checkPointDao.insert(firstCheckpoint));
                    for(Region region: regionList) {
                        executor.submit(() -> {
                            try {
                                Record record = new Record();
                                record.setRegion(region);
                                record.setCheckPoint(firstCheckpoint);
                                recordDao.insert(record);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        });
                    }
                    return;
                }
                System.out.println("Find new checkpoints");
                Long newestCheckpoint = rainViewerDao.getNewCheckPoint();

                int step = (int) ((newestCheckpoint - lastCheckPoint.getTimestamp()) / TimeUnit.HOURS.toSeconds(1));
                if(step > 0) {
                    List<Region> regionList = regionDao.listRegions();
                    if(regionList == null || regionList.size() == 0) {
                        return;
                    }
                    for(int i=1; i <= step; i++) {
                        CheckPoint newCheckpoint = new CheckPoint(lastCheckPoint.getTimestamp() + TimeUnit.HOURS.toSeconds(i));
                        newCheckpoint.setId(checkPointDao.insert(newCheckpoint));
                        for(Region region: regionList) {
                            executor.submit(() -> {
                                try {
                                    Record record = new Record();
                                    record.setRegion(region);
                                    record.setCheckPoint(newCheckpoint);
                                    recordDao.insert(record);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0,1, TimeUnit.HOURS);
    }
    public void run() {
        AtomicBoolean isAvailable = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> isAvailable.set(false)));

        try {
            while (isAvailable.get()) {
                System.out.println("Fetching record to process");
                List<Record> recordList = recordDao.listUnProcessedRecords();
                if(recordList == null || recordList.size() == 0) {
                    Thread.sleep(100000l);
                    continue;
                }

                for(Record record : recordList) {
                   if(record.getData() != null) {
                       return;
                   }
                   executor.submit(() -> {
                       try {
                           RainViewerParam param = new RainViewerParam(record.getRegion(),record.getCheckPoint().getTimestamp());
                           byte[] data = rainViewerDao.getSnapshot(param);
                           record.setData(data);
                           recordDao.insert(record);
                       }catch (Exception e) {
                           e.printStackTrace();
                       }
                   });
                }
                Thread.sleep(100000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
