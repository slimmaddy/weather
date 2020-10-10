package vn.com.vng.WeatherMonitor.layer.application.entity;

public class CheckPoint {
    private Integer id;

    private Long timestamp;

    public CheckPoint() {
    }

    public CheckPoint(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
