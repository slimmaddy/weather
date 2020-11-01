package vn.com.vng.WeatherMonitor.layer.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import vn.com.vng.WeatherMonitor.layer.application.entity.CheckPoint;
import vn.com.vng.WeatherMonitor.layer.application.model.CheckPointRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
public class CheckPointDao {
    @Autowired
    @Qualifier("mysqlTemplate")
    private JdbcTemplate mysqlTemplate;

    public static final String checkpointTable = "checkpoint_tbl";

    public Integer insert(CheckPoint checkPoint) throws Exception {
        int maxRetry = 3;
        while (maxRetry-- > 0) {
            try {
                String sql = String.format(
                        "INSERT INTO %s (timestamp) VALUES (?)", checkpointTable);

                KeyHolder keyHolder = new GeneratedKeyHolder();

                mysqlTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, checkPoint.getTimestamp());
                    return ps;
                }, keyHolder);
                return keyHolder.getKey().intValue();
            } catch (DeadlockLoserDataAccessException e) {
                if (maxRetry == 0) {
                    throw e;
                }
            }
        }
        throw new Exception("Out of retry times when inserting token");
    }

    public List<CheckPoint> listCheckPoints() {
        String sql = String.format("SELECT * FROM %s ", checkpointTable);
        return mysqlTemplate.query(sql, new CheckPointRowMapper());
    }

    public CheckPoint getNewestCheckpoint() {
        String sql = String.format("SELECT * FROM %s order by timestamp DESC limit 1", checkpointTable);
        List<CheckPoint> checkPoints = mysqlTemplate.query(sql, new CheckPointRowMapper());
        if(checkPoints == null || checkPoints.size() == 0) {
            return null;
        }
        return checkPoints.get(0);
    }

}
