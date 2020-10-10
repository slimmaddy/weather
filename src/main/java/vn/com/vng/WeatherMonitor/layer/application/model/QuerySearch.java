package vn.com.vng.WeatherMonitor.layer.application.model;

public class QuerySearch extends QueryFilter {
    private String regionCode;
    private String areaCode;

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
