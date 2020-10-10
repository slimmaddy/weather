package vn.com.vng.WeatherMonitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.com.vng.WeatherMonitor.layer.infastructure.JdbcMysqlAdapter;

import javax.sql.DataSource;

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

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("common-pool::");
        executor.initialize();
        return executor;
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
