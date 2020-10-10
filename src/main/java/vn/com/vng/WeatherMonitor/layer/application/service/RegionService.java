package vn.com.vng.WeatherMonitor.layer.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.vng.WeatherMonitor.layer.application.dao.RegionDao;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.RegionResponsePayload;
import vn.com.vng.WeatherMonitor.layer.application.validator.RegionValidator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RegionService {
    @Autowired
    RegionDao regionDao;

    public Region insertRegion(Region region) throws Exception {
        RegionValidator.validate(region);
        regionDao.insert(region);
        return region;
    }

    public List<RegionResponsePayload> listRegion() {
       List<Region> regionList = regionDao.listRegions();
       List<Area> areaList = regionDao.listArea();
       return Stream.concat(regionList.stream().map(RegionResponsePayload::new),
               areaList.stream().map(RegionResponsePayload::new))
               .collect(Collectors.toList());
    }
}
