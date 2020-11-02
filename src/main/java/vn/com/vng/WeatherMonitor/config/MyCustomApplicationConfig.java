package vn.com.vng.WeatherMonitor.config;

import org.springframework.jdbc.core.JdbcTemplate;
import vn.com.vng.WeatherMonitor.layer.infastructure.JdbcMysqlAdapter;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyCustomApplicationConfig {

    private static volatile MyCustomApplicationConfig instance;

    public MyCustomApplicationConfig() {
    }

    public static synchronized MyCustomApplicationConfig getInstance() {
        if (instance == null) {
            instance = new MyCustomApplicationConfig();
        }
        return instance;
    }

    private JdbcMysqlAdapter jdbcMysqlAdapter = new JdbcMysqlAdapter();

    private Settings settings = Settings.getInstance();

    private ExecutorService executor = Executors.newFixedThreadPool(settings.THREAD_POOL_SIZE);

    public ExecutorService taskExecutor() {
        return executor;
    }

    public DataSource mysqlKingHubReadDatasource() {
        return jdbcMysqlAdapter.mysqlKingHubReadDatasource();
    }

    public JdbcTemplate mysqlJdbcTemplate() {
        return jdbcMysqlAdapter.getJdbcTemplate();
    }

}