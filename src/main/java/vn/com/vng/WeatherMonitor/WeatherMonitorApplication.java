package vn.com.vng.WeatherMonitor;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.FetchRainDataThread;
import vn.com.vng.WeatherMonitor.layer.application.dao.RecordDao;
import vn.com.vng.WeatherMonitor.layer.application.entity.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class WeatherMonitorApplication implements CommandLineRunner {
	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		Settings setting = Settings.getInstance();
		Integer model = getModel(args);
		WebApplicationType webType = WebApplicationType.NONE;
		switch (model) {
			case -1:
				webType = WebApplicationType.SERVLET;
				break;
		}
		try {
			new SpringApplicationBuilder(WeatherMonitorApplication.class).web(webType)
					.registerShutdownHook(true).run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run(String... args) throws Exception {
		WeatherMonitorApplication app = context.getBean(WeatherMonitorApplication.class);
		Integer model = getModel(args);
		System.out.println("model: " + model);
		try {
			switch (model) {
				case 1:
					migrateRecordData();
					break;
				default:
					app.runServer();
			}
			System.out.println("TAATTTTTTT");
			System.exit(0);
		} catch (Throwable e) {
			System.exit(1);
		}
	}

	private void runServer() throws InterruptedException {
		ExecutorService executor = Executors.newCachedThreadPool();

		executor.submit(context.getBean(FetchRainDataThread.class));
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	}

	private void migrateRecordData() throws Exception {
		System.out.println("Start migrating data");
		RecordDao recordDao = context.getBean(RecordDao.class);

//		List<Record> listRecord = recordDao.listProcessedRecords();
//		System.out.println("total record :" + listRecord.size());
//		for(Record r : listRecord) {
//			if(r.getData() != null) {
//				long score = 0;
//				for(byte i : r.getData() ) {
//					score += (i & 0xFF);
//				}
//				r.setScore(score);
//				System.out.println(score);
//				recordDao.insert(r);
//			}
//		}
	}

	private static Integer getModel(String[] args) {
		Options options = new Options();

		Option modelOpt = new Option("m", "model", true, "model option");

		modelOpt.setRequired(false);

		options.addOption(modelOpt);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}

		int model = -1;
		try {
			model = Integer.parseInt(cmd.getOptionValue("model"));
		} catch (Exception e) {
		}
		return model;
	}
}
