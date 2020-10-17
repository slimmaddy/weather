package vn.com.vng.WeatherMonitor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class stores dynamic setting. Created by vietpd on 18/06/2018.
 */
public class Settings {
    private static volatile Settings instance = null;
    private static Object mutex = new Object();

    public int PORT = 8000;
    public String PREFIX_PATH = "";
    public String TOKEN = "";
    public String PROXY_HOST = "localhost";
    public int PROXY_PORT = 0;
    public String PROXY_SCHEME = "http";
    public String MYSQL_IP = "localhost";
    public int MYSQL_PORT = 3306;
    public String MYSQL_DB = "weather";
    public String MYSQL_USER = "root";
    public String MYSQL_PASS = "root";
    public int MYSQL_POOL_SIZE = 3;
    public int THREAD_POOL_SIZE = 5;



    public static Settings getInstance() {
        Settings result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                        File file = new File("settings.yaml");
                        System.out.println("Overiding Application Settings:");
                        System.out.println(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                        System.out.println("##################\n");

                        instance = result = mapper.readValue(file, Settings.class);
                    } catch (FileNotFoundException e1) {
                        instance = result = new Settings();
                    } catch (Exception e) {
                        instance = result = new Settings();
                    }
                }
            }
        }
        return result;
    }
}
