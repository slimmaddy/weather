package vn.com.vng.WeatherMonitor.layer.application.validator;

import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.QueryFilter;
import vn.com.vng.WeatherMonitor.layer.application.model.QuerySearch;

import java.util.concurrent.TimeUnit;

public class QuerySearchValidator {
    public static void validate(QuerySearch filter) throws InvalidDataException {
        long now = System.currentTimeMillis()/1000;

        if(filter.getRegionCode() == null && filter.getAreaCode() == null) {
            throw new InvalidDataException("[region] or [area] required");
        }
        if(filter.getFromDate() == null && filter.getToDate() == null) {
            filter.setToDate(now);
        }

        if(filter.getFromDate() == null) {
            filter.setFromDate(filter.getToDate() - TimeUnit.DAYS.toSeconds(1));
        }

        if(filter.getFromDate() > now) {
            throw new InvalidDataException("[from] over current time");
        }
        if(filter.getToDate() == null) {
            filter.setToDate(filter.getFromDate() + TimeUnit.DAYS.toSeconds(1));
        }
    }
}
