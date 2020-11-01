package vn.com.vng.WeatherMonitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.com.vng.WeatherMonitor.layer.infastructure.JdbcMysqlAdapter;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class MyCustomApplicationConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> , WebMvcConfigurer {

    @Autowired
    private MyCustomRequestInterceptor customRequestInterceptor;

    private JdbcMysqlAdapter jdbcMysqlAdapter = new JdbcMysqlAdapter();

    private Settings settings = Settings.getInstance();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(customRequestInterceptor).addPathPatterns("/**").excludePathPatterns("/error", "/ping");
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setContextPath(settings.PREFIX_PATH);
        factory.setPort(settings.PORT);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        assert configurer != null;
        configurer.setTaskExecutor(mvcTaskExecutor());
        configurer.setDefaultTimeout(60000);
    }

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
