package vn.com.vng.WeatherMonitor.layer.application.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.RainViewerParam;
import vn.com.vng.WeatherMonitor.ultility.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.InputStream;

public class RainViewerDao {
    static final int TIMEOUT = 10000;
    static final int RETRY = 2;
    static final int TIMEWAIT = 500;

    private PoolingHttpClientConnectionManager connManager = Util.createHttpConnManager();
    private ConnectionKeepAliveStrategy strategy = Util.createKeepAliveStrategy();
    private RequestConfig config = Util.createRequestConfig(TIMEOUT);
    private HttpRequestRetryHandler retryHandler = Util.createRequestRetryHandler(RETRY);
    private HttpRoutePlanner routePlanner = Util.createRoutePlanner();
    private ServiceUnavailableRetryStrategy retryStrategy = Util.createServiceUnavailableRetryStrategy(RETRY,TIMEWAIT);
    private CloseableHttpClient httpClient = HttpClients.custom().setKeepAliveStrategy(strategy).setConnectionManager(connManager)
            .setDefaultRequestConfig(config).setRetryHandler(retryHandler)
            .setServiceUnavailableRetryStrategy(retryStrategy).setRoutePlanner(routePlanner).build();

    private static final String rootPath = "https://tilecache.rainviewer.com/v2/radar/";

    private static volatile RainViewerDao instance;

    public RainViewerDao() {
    }

    public static synchronized RainViewerDao getInstance() {
        if (instance == null) {
            instance = new RainViewerDao();
        }
        return instance;
    }

    public byte[] getSnapshot(RainViewerParam param) throws Exception {
        try {
            ParamValidator.validate(param);
            String url = String.format(rootPath+"%d/%d/%d/%f/%f/%d/%s.png", param.getTimestamp(), param.getSize(),
                    param.getRegion().getZoom(), param.getRegion().getLatitude(), param.getRegion().getLongitude(), param.getColor(), param.getOption());
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse result = httpClient.execute(request)) {
                switch (result.getStatusLine().getStatusCode()) {
                    case 200:
                        break;
                    default:
                        throw new Exception("Status: " + result.getStatusLine().getStatusCode());
                }
                HttpEntity entity = result.getEntity();
                InputStream is = entity.getContent();
                BufferedImage bufferedImage = ImageIO.read(is);
                return getGreyValue(bufferedImage);
            }
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public Long getNewCheckPoint() {
        try {
            String url = "https://api.rainviewer.com/public/maps.json";
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse result = httpClient.execute(request)) {
                switch (result.getStatusLine().getStatusCode()) {
                    case 200:
                        break;
                    default:
                        throw new Exception("Status: " + result.getStatusLine().getStatusCode());
                }
                String jsonString = EntityUtils.toString(result.getEntity(), "UTF-8");
                JSONArray data = new JSONArray(jsonString);
                if(data.length() == 0 ) {
                    throw new Exception("no new checkpoint available");
                }
                return data.getLong(data.length()-1);
            }
        } catch (Exception e) {
            return -1L;
        }
    }

    public byte[] getGreyValue(BufferedImage img) throws JsonProcessingException {
        int width = img.getWidth();
        int height = img.getHeight();
        byte[] imgArr = new byte[width * height];
        Raster raster = img.getData();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                imgArr[i*width+j] = (byte) raster.getSample(j, i, 0);
            }
        }
        return imgArr;
    }

//    public static void main(String[] args) throws Exception {
//        RainViewerDao rainViewerDao = new RainViewerDao();
//        Region region = new Region("Hanoi", "HN", 10, -77.670344f, 127.320496f);
//        byte[] data = rainViewerDao.getSnapshot(new RainViewerParam(region, 1601893800));
//        for(int i = 0; i < data.length; i++) {
//            int tmp = data[i] & 0xFF;
//            System.out.print(tmp);
//        }
//        System.out.println(rainViewerDao.getNewCheckPoint());
//    }

    static class ParamValidator {
        public static void validate(RainViewerParam rainViewerParam) throws InvalidDataException {
            if(rainViewerParam == null) {
                throw new InvalidDataException("param is null");
            }
            if(rainViewerParam.getRegion() == null) {
                throw new InvalidDataException("[region] is required");
            }
            if(rainViewerParam.getRegion().getLatitude() == null) {
                throw new InvalidDataException("[region.latitude] is required");
            }
            if(rainViewerParam.getRegion().getLongitude() == null) {
                throw new InvalidDataException("[region.longitude] is required");
            }
            if(rainViewerParam.getTimestamp() == null) {
                throw new InvalidDataException("[timestamp] is required");
            }
            if(rainViewerParam.getColor() == null) {
                rainViewerParam.setColor(0);
            }
            if(rainViewerParam.getSize() == null) {
                rainViewerParam.setSize(512);
            }
            if(rainViewerParam.getOption() == null) {
                rainViewerParam.setOption("0_1");
            } else {
                String[] parts = rainViewerParam.getOption().split("_");
                if(parts.length != 2) {
                    throw new InvalidDataException("[option] wrong format:" + rainViewerParam.getOption());
                }
                try {
                    Integer smooth = Integer.parseInt(parts[0]);
                    Integer snow = Integer.parseInt(parts[1]);
                    if(smooth != 0 && smooth != 1) {
                        throw new InvalidDataException("[option] wrong format:" + rainViewerParam.getOption());
                    }
                    if(snow != 0 && snow != 1) {
                        throw new InvalidDataException("[option] wrong format:" + rainViewerParam.getOption());
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("[option] wrong format:" + rainViewerParam.getOption());
                } catch (InvalidDataException e) {
                    throw e;
                }
            }
        }
    }
}
