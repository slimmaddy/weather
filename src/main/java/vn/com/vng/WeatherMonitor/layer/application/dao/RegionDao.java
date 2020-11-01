package vn.com.vng.WeatherMonitor.layer.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import vn.com.vng.WeatherMonitor.layer.application.entity.Area;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.AreaRowMapper;
import vn.com.vng.WeatherMonitor.layer.application.model.RegionRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class RegionDao {
    @Autowired
    @Qualifier("mysqlTemplate")
    private JdbcTemplate mysqlTemplate;

    public static final String regionTable = "region_tbl";
    public static final String areaTable = "area_tbl";

    public Integer insert(Region region) throws Exception {
        int maxRetry = 3;
        while (maxRetry-- > 0) {
            try {
                String sql = String.format(
                        "INSERT INTO %s (name, code, latitude, longitude, zoom, area_id) VALUES (?, ?, ?, ?, ?, ?)", regionTable);

                KeyHolder keyHolder = new GeneratedKeyHolder();

                mysqlTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, region.getName());
                    ps.setString(2, region.getCode());
                    ps.setFloat(3, region.getLatitude());
                    ps.setFloat(4, region.getLongitude());
                    ps.setInt(5, region.getZoom());
                    ps.setInt(6, region.getArea().getId());
                    return ps;
                }, keyHolder);
                return keyHolder.getKey().intValue();
            } catch (DeadlockLoserDataAccessException e) {
                if (maxRetry == 0) {
                    throw e;
                }
            }
        }
        throw new Exception("Out of retry times when inserting region");
    }

    public Integer insert(Area area) throws Exception {
        int maxRetry = 3;
        while (maxRetry-- > 0) {
            try {
                String sql = String.format(
                        "INSERT INTO %s (name, code, area_id) VALUES (?, ?, ?)", areaTable);

                KeyHolder keyHolder = new GeneratedKeyHolder();

                mysqlTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, area.getName());
                    ps.setString(2, area.getCode());
                    ps.setInt(3,area.getParentID());
                    return ps;
                }, keyHolder);
                return keyHolder.getKey().intValue();
            } catch (DeadlockLoserDataAccessException e) {
                if (maxRetry == 0) {
                    throw e;
                }
            }
        }
        throw new Exception("Out of retry times when inserting region");
    }

    public List<Region> listRegions() {
        String sql = String.format("SELECT * FROM %s a inner join %s b on a.area_id = b.id", regionTable, areaTable);
        return mysqlTemplate.query(sql, new RegionRowMapper());
    }

    public List<Area> listArea() {
        String sql = String.format("SELECT * FROM %s ", areaTable);
        return mysqlTemplate.query(sql, new AreaRowMapper());
    }

    public Region getRegionByCode(String code) {
        String sql = String.format("SELECT * FROM %s a inner join %s b on a.area_id = b.id where a.code = ?", regionTable, areaTable);
        List<Object> params = new ArrayList<>();
        params.add(code);
        List<Region> regionList = mysqlTemplate.query(sql, params.toArray(), new RegionRowMapper());
        if(regionList == null || regionList.size() == 0 ) {
            return null;
        }
        return regionList.get(0);
    }

    public Area getAreaByCode(String code) {
        String sql = String.format("SELECT * FROM %s where code = ?", areaTable);
        List<String> params = new ArrayList<>();
        params.add(code);
        List<Area> areaList = mysqlTemplate.query(sql, params.toArray(), new AreaRowMapper());
        if(areaList == null || areaList.size() == 0 ) {
            return null;
        }
        return areaList.get(0);
    }

    public List<Region> getAllRegionsByArea(Integer parentAreaID) {
        List<Region> regionList = new ArrayList<>();
        List<Area> subAreaList = this.getSubArea(parentAreaID);
        if(subAreaList == null || subAreaList.size() == 0) {
            regionList.addAll(getRegionsByArea(parentAreaID));
            return regionList;
        }
        for(Area area : subAreaList) {
            regionList.addAll(this.getAllRegionsByArea(area.getId()));
        }
        return regionList;
    }

    public List<Region> getRegionsByArea(Integer areaID) {
        String sql = String.format("SELECT * FROM %s a inner join %s b on a.area_id = b.id where b.id = ?", regionTable, areaTable);
        List<Object> params = new ArrayList<>();
        params.add(areaID);
        return mysqlTemplate.query(sql, params.toArray(), new RegionRowMapper());
    }

    public List<Area> getSubArea(Integer parentAreaID) {
        String sql = String.format("SELECT * FROM %s where area_id = ?", areaTable);
        List<Object> params = new ArrayList<>();
        params.add(parentAreaID);
        return mysqlTemplate.query(sql, params.toArray(), new AreaRowMapper());
    }

}
