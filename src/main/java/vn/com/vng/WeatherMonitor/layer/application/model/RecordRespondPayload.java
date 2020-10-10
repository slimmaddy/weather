package vn.com.vng.WeatherMonitor.layer.application.model;

import org.apache.commons.beanutils.PropertyUtils;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;

import java.lang.reflect.InvocationTargetException;

public class RecordRespondPayload extends Record {
    private long score;
    private String regionCode;
    private String areaCode;
    private long timestamp;

    public RecordRespondPayload(Record record){
        try {
            PropertyUtils.copyProperties(this, record);

            long score = 0;
            if(getData() != null) {
                for(byte i : getData()) {
                    score += (i & 0xFF);
                }
            }
            this.score = score;
            this.regionCode = getRegion().getCode();
            this.timestamp = getCheckPoint().getTimestamp();

            setRegion(null);
            setCheckPoint(null);
            setData(null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecordRespondPayload() {
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
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
