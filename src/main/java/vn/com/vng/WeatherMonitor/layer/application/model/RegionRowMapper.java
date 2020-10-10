package vn.com.vng.WeatherMonitor.layer.application.model;

import org.springframework.jdbc.core.RowMapper;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.CheckPoint;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegionRowMapper implements RowMapper<Region> {
    @Override
    public Region mapRow(ResultSet rs, int rowNum) throws SQLException {
        Region region = new Region();

        Area area = new Area();
        area.setId(rs.getInt("b.id"));
        area.setName(rs.getString("b.name"));
        area.setCode(rs.getString("b.code"));
        area.setParentID(rs.getInt("b.area_id"));

        region.setId(rs.getInt("a.id"));
        region.setName(rs.getString("a.name"));
        region.setCode(rs.getString("a.code"));
        region.setLatitude(rs.getFloat("latitude"));
        region.setLongitude(rs.getFloat("longitude"));
        region.setZoom(rs.getInt("zoom"));
        region.setArea(area);
        return region;
    }
}
