package vn.com.vng.WeatherMonitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.context.request.RequestContextListener;
import vn.com.vng.WeatherMonitor.layer.infastructure.JdbcMysqlAdapter;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan(value={"vn.com.vng.WeatherMonitor"})
@EnableAsync
public class MyCustomApplicationConfig {

    private JdbcMysqlAdapter jdbcMysqlAdapter = new JdbcMysqlAdapter();

    private Settings settings = Settings.getInstance();

    @Bean
    public AsyncTaskExecutor mvcTaskExecutor() {
        return new ConcurrentTaskExecutor(Executors.newCachedThreadPool());
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

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