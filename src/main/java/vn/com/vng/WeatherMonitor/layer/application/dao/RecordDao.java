package vn.com.vng.WeatherMonitor.layer.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.QueryFilter;
import vn.com.vng.WeatherMonitor.layer.application.model.RecordRowMapper;
import vn.com.vng.WeatherMonitor.layer.application.model.RegionRowMapper;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static vn.com.vng.WeatherMonitor.layer.application.dao.CheckPointDao.checkpointTable;
import static vn.com.vng.WeatherMonitor.layer.application.dao.RegionDao.regionTable;

@Component
public class RecordDao {
    @Autowired
    @Qualifier("mysqlTemplate")
    private JdbcTemplate mysqlTemplate;

    public static final String recordTable = "record_tbl";

    public void insert(Record record) throws Exception {
        int maxRetry = 3;
        while (maxRetry-- > 0) {
            try {
                if(record.getData() == null) {
                    String sql = String.format(
                            "INSERT INTO %s (region_id, checkpoint_id) VALUES (?, ?)", recordTable);

                    mysqlTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql);
                        ps.setInt(1, record.getRegion().getId());
                        ps.setInt(2, record.getCheckPoint().getId());
                        return ps;
                    });
                    return;
                } else {
                    try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(record.getData())) {
                        String sql = String.format(
                                "REPLACE INTO %s (region_id, checkpoint_id, data) VALUES (?, ?, ?)", recordTable);

                        mysqlTemplate.update(connection -> {
                            PreparedStatement ps = connection.prepareStatement(sql);
                            ps.setInt(1, record.getRegion().getId());
                            ps.setInt(2, record.getCheckPoint().getId());
                            ps.setBlob(3, byteArrayInputStream);
                            return ps;
                        });
                    }
                    return;
                }
            } catch (DeadlockLoserDataAccessException e) {
                if (maxRetry == 0) {
                    throw e;
                }
            }
        }
        throw new Exception("Out of retry times when inserting token");
    }

    public List<Record> listRecords() {
        String sql = String.format("SELECT * FROM %s as record_tbl " +
                "Inner join %s as region_tbl on record_tbl.region_id = region_tbl.id " +
                "Inner join %s as checkpoint_tbl on record_tbl.checkpoint_id = checkpoint_tbl.id", recordTable, regionTable, checkpointTable);
        System.out.println(sql);
        return mysqlTemplate.query(sql, new RecordRowMapper());
    }

    public List<Record> listUnProcessedRecords() {
        String sql = String.format("SELECT * FROM %s as record_tbl " +
                "Inner join %s as region_tbl on record_tbl.region_id = region_tbl.id " +
                "Inner join %s as checkpoint_tbl on record_tbl.checkpoint_id = checkpoint_tbl.id " +
                "where data is null", recordTable, regionTable, checkpointTable);
        List<Record> list = mysqlTemplate.query(sql, new RecordRowMapper());
        return list;
    }

    public List<Record> listRecordsByRegion(Region region) {
        String sql = String.format("SELECT * FROM %s as record_tbl " +
                "Inner join %s as region_tbl on record_tbl.region_id = region_tbl.id " +
                "Inner join %s as checkpoint_tbl on record_tbl.checkpoint_id = checkpoint_tbl.id " +
                "where record_tbl.region_id = ?", recordTable, regionTable, checkpointTable);
        List<Object> params = new ArrayList<>();
        params.add(region.getId());
        return mysqlTemplate.query(sql, params.toArray(), new RecordRowMapper());
    }

    public List<Record> listRecordByFilter(QueryFilter filter) {
//        //query area record
//        if(filter.getRegion().getId() == null) {
//            String sql = String.format("SELECT * FROM %s as record_tbl " +
//                    "Inner join %s as region_tbl on record_tbl.region_id = region_tbl.id " +
//                    "Inner join %s as checkpoint_tbl on record_tbl.checkpoint_id = checkpoint_tbl.id " +
//                    "where region_tbl.area_id = ? " +
//                    "and checkpoint_tbl.timestamp >= ? " +
//                    "and checkpoint_tbl.timestamp <= ?", recordTable, regionTable, checkpointTable);
//            List<Object> params = new ArrayList<>();
//            params.add(filter.getRegion().getArea().getId());
//            params.add(filter.getFromDate());
//            params.add(filter.getToDate());
//            return mysqlTemplate.query(sql, params.toArray(), new RecordRowMapper());
//        }
        //query region record
        String sql = String.format("SELECT * FROM %s as record_tbl " +
                "Inner join %s as region_tbl on record_tbl.region_id = region_tbl.id " +
                "Inner join %s as checkpoint_tbl on record_tbl.checkpoint_id = checkpoint_tbl.id " +
                "where record_tbl.region_id = ? " +
                "and checkpoint_tbl.timestamp >= ? " +
                "and checkpoint_tbl.timestamp <= ?", recordTable, regionTable, checkpointTable);
        List<Object> params = new ArrayList<>();
        params.add(filter.getRegion().getId());
        params.add(filter.getFromDate());
        params.add(filter.getToDate());
        return mysqlTemplate.query(sql, params.toArray(), new RecordRowMapper());
    }
}
