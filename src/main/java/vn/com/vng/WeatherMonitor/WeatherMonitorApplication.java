package vn.com.vng.WeatherMonitor;

import com.mysql.cj.x.protobuf.MysqlxCursor;
import spark.Spark;
import vn.com.vng.WeatherMonitor.config.MyCustomApplicationConfig;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.FetchRainDataThread;
import vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI.RecordController;
import vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI.RegionController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WeatherMonitorApplication  {
	private static Settings  setting = Settings.getInstance();
//	private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomApplicationConfig.class);
	public static void main(String[] args) {
		try {
			igniteSpark();
			runService();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void runService() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		FetchRainDataThread fetchRainDataThread = new FetchRainDataThread();
		executor.submit(fetchRainDataThread);
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	}
	private static void igniteSpark() {
		Spark.port(setting.PORT);
		Spark.threadPool(5, 5, 30000);
		Spark.path(setting.PREFIX_PATH + "/record", new RecordController());
		Spark.path(setting.PREFIX_PATH + "/region", new RegionController());
		Spark.init();
		Spark.get("/", (req, res) -> {
			return "";
		});

		System.out.println("Server started up");
	}
}
