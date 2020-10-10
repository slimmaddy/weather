package vn.com.vng.WeatherMonitor.layer.application.model;

import vn.com.vng.WeatherMonitor.layer.application.entity.Region;

public class QueryFilter {
    private Long fromDate;
    private Long toDate;

    private Region region;

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
