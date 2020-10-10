package vn.com.vng.WeatherMonitor.layer.application.model;

import vn.com.vng.WeatherMonitor.layer.application.entity.Region;

public class RainViewerParam {
    private Region region;
    private Long timestamp;
    private Integer size = 512;
    private String option = "0_1";
    private Integer color = 0;

    public RainViewerParam(Region region, long timestamp) {
        this.timestamp = timestamp;
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }



    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
