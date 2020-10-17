package vn.com.vng.WeatherMonitor.layer.application.model;

import org.springframework.jdbc.core.RowMapper;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.CheckPoint;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RecordRowMapper implements RowMapper<Record> {

    @Override
    public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
        Record record = new Record();

        Region region = new Region();
        region.setId(rs.getInt("region_id"));
        region.setName(rs.getString("name"));
        region.setCode(rs.getString("code"));
        region.setLatitude(rs.getFloat("latitude"));
        region.setLongitude(rs.getFloat("longitude"));
        region.setZoom(rs.getInt("zoom"));

        CheckPoint checkPoint = new CheckPoint();
        checkPoint.setId(rs.getInt("checkpoint_id"));
        checkPoint.setTimestamp(rs.getLong("timestamp"));

//        if(rs.getBlob("data") != null) {
//            Blob blob = rs.getBlob("data");
//            record.setData(blob.getBytes(1L, (int) blob.length()));
//        }
        if(rs.getObject("score") != null) {
            record.setScore(rs.getLong("score"));
        } else {
            record.setScore(null);
        }

        record.setRegion(region);
        record.setCheckPoint(checkPoint);

        return record;
    }
}
