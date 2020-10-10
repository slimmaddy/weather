package vn.com.vng.WeatherMonitor.layer.application.model;

import org.springframework.jdbc.core.RowMapper;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AreaRowMapper implements RowMapper<Area> {
    @Override
    public Area mapRow(ResultSet rs, int rowNum) throws SQLException {
        Area area = new Area();
        area.setId(rs.getInt("id"));
        area.setName(rs.getString("name"));
        area.setCode(rs.getString("code"));
        area.setParentID(rs.getInt("area_id"));

        return area;
    }
}
