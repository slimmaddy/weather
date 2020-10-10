package vn.com.vng.WeatherMonitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import vn.com.vng.WeatherMonitor.layer.application.FetchRainDataThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class WeatherMonitorApplication implements CommandLineRunner {
	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		new SpringApplicationBuilder(WeatherMonitorApplication.class).web(WebApplicationType.SERVLET)
				.registerShutdownHook(true).run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();

		executor.submit(context.getBean(FetchRainDataThread.class));
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}
}
