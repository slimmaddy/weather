package vn.com.vng.WeatherMonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import vn.com.vng.WeatherMonitor.layer.infastructure.JdbcMysqlAdapter;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan(value={"vn.com.vng.WeatherMonitor"})
public class MyCustomApplicationConfig {

    private JdbcMysqlAdapter jdbcMysqlAdapter = new JdbcMysqlAdapter();

    private Settings settings = Settings.getInstance();

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(settings.THREAD_POOL_SIZE);
    }

    @Bean(name = "mysqlDatasource", destroyMethod = "")
    public DataSource mysqlKingHubReadDatasource() {
        return jdbcMysqlAdapter.mysqlKingHubReadDatasource();
    }

    @Bean(name = "mysqlTemplate")
    JdbcTemplate mysqlJdbcTemplate() {
        return jdbcMysqlAdapter.getJdbcTemplate();
    }

}