package vn.com.vng.WeatherMonitor.layer.application.model;

import org.apache.commons.beanutils.PropertyUtils;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;

public class RegionResponsePayload extends Region {
    private Boolean isProvince;
    public RegionResponsePayload(Region region) {
        try {
            PropertyUtils.copyProperties(this, region);
            getArea().setParentID(null);
            getArea().setId(null);
            setId(null);
            setLongitude(null);
            setLatitude(null);
            setZoom(null);
            setProvince(true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RegionResponsePayload(Area area) {
        setCode(area.getCode());
        setName(area.getName());
        setProvince(false);
    }

    public Boolean getProvince() {
        return isProvince;
    }

    public void setProvince(Boolean province) {
        isProvince = province;
    }
}
