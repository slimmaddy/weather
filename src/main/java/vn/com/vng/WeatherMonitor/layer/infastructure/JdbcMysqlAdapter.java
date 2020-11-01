package vn.com.vng.WeatherMonitor.layer.infastructure;

import com.mysql.jdbc.Driver;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import vn.com.vng.WeatherMonitor.config.Settings;

import javax.sql.DataSource;
import java.sql.SQLException;

public class JdbcMysqlAdapter {

    private DataSource dataSource;

    private Settings settings = Settings.getInstance();

    public JdbcMysqlAdapter() {
        dataSource = mysqlKingHubReadDatasource();
    }

    public DataSource mysqlKingHubReadDatasource() {

        BasicDataSource dataSource = new BasicDataSource();
        String connectionString = String.format("jdbc:mysql://%s:%d/%s?useSSL=false", settings.MYSQL_IP,
                settings.MYSQL_PORT, settings.MYSQL_DB);

        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUsername(settings.MYSQL_USER);
        dataSource.setPassword(settings.MYSQL_PASS);
        dataSource.setUrl(connectionString);
        dataSource.setInitialSize(settings.MYSQL_POOL_SIZE);
        dataSource.setMaxTotal(settings.MYSQL_POOL_SIZE);

        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQueryTimeout(3);
        dataSource.setValidationQuery("SELECT 1");

        dataSource.setMaxConnLifetimeMillis(900000);
        dataSource.setTimeBetweenEvictionRunsMillis(300000);
        dataSource.setLogExpiredConnections(false);

        dataSource.setCacheState(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setPoolPreparedStatements(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));

        return dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(false);
        return jdbcTemplate;
    }
}
