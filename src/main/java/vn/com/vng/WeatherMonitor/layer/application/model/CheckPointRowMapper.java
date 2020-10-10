package vn.com.vng.WeatherMonitor.layer.application.model;

import org.springframework.jdbc.core.RowMapper;
import vn.com.vng.WeatherMonitor.layer.application.entity.CheckPoint;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckPointRowMapper implements RowMapper<CheckPoint>{

    @Override
    public CheckPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
        CheckPoint checkPoint = new CheckPoint();

        checkPoint.setId(rs.getInt("id"));
        checkPoint.setTimestamp(rs.getLong("timestamp"));

        return checkPoint;
    }

}
