package vn.com.vng.WeatherMonitor;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spark.Spark;
import vn.com.vng.WeatherMonitor.config.MyCustomApplicationConfig;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.FetchRainDataThread;
import vn.com.vng.WeatherMonitor.layer.application.dao.RecordDao;
import vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI.RecordController;
import vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI.RegionController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WeatherMonitorApplication  {


	public static void main(String[] args) throws InterruptedException {
		try {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomApplicationConfig.class);
			Settings setting = Settings.getInstance();

			Spark.port(setting.PORT);
			Spark.threadPool(2, 1, 3000);
			Spark.path(setting.PREFIX_PATH + "/record", context.getBean(RecordController.class));
			Spark.path(setting.PREFIX_PATH + "/region", context.getBean(RegionController.class));

			Spark.init();

			ExecutorService executor = Executors.newCachedThreadPool();
			executor.submit(context.getBean(FetchRainDataThread.class));
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
