package vn.com.vng.WeatherMonitor.layer.application.model;

import org.apache.commons.beanutils.PropertyUtils;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;

public class RecordRespondPayload extends Record {
    private String regionCode;
    private long timestamp;

    public RecordRespondPayload(Record record){
        try {
            PropertyUtils.copyProperties(this, record);

            this.regionCode = getRegion().getCode();
            this.timestamp = getCheckPoint().getTimestamp();

            setRegion(null);
            setCheckPoint(null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecordRespondPayload() {
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
