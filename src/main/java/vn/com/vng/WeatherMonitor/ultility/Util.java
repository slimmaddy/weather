package vn.com.vng.WeatherMonitor.ultility;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.ClassUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.com.vng.WeatherMonitor.config.Settings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;


public class Util {

    public static String CLEAR_CHAR = "\033[F\033[K";
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static ObjectMapper OBJECT_MAPPER_WITH_NULL = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static void clearCurrentConsoleLine() {
        System.out.print(CLEAR_CHAR);
    }

    private static Object formatResult(Object result) throws Throwable {
        if (result == null) {
            return null;
        }
        if (result instanceof String) {
            return result;
        }
        if (result instanceof Boolean) {
            return result;
        }
        if (result instanceof JSONObject) {
            return result;
        }
        if (result instanceof JSONArray) {
            return result;
        }
        if (ClassUtils.isPrimitiveOrWrapper(result.getClass())) {
            return result;
        }
        String jsonString = OBJECT_MAPPER.writeValueAsString(result);

        return new JSONObject(jsonString);
    }

    public static ResponseEntity<String> formatResponse(HttpStatus statusCode, Object result, int status,
                                                        String message) {
        try {
            JSONObject response = new JSONObject();
            response.put("code", statusCode.value());
            response.put("result", formatResult(result));

            response.put("status", status);
            response.put("message", message);

            return ResponseEntity.status(statusCode).body(response.toString());
        } catch (Throwable e) {
            e.printStackTrace();

            JSONObject response = new JSONObject();
            response.put("code", statusCode);
            response.put("status", status);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
        }
    }

    public static ResponseEntity<String> formatResponse(HttpStatus statusCode, Collection<?> result, int status,
                                                        String message) {
        try {
            JSONObject response = new JSONObject();
            response.put("code", statusCode.value());
            if (result != null) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(Include.NON_NULL);
                String jsonString = mapper.writeValueAsString(result);
                response.put("result", new JSONArray(jsonString));
            }

            response.put("status", status);
            response.put("message", message);

            return ResponseEntity.status(statusCode).body(response.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            JSONObject response = new JSONObject();
            response.put("code", statusCode);
            response.put("status", status);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
        }
    }
    public static ResponseEntity<InputStreamResource> formatResourceResponse(HttpStatus statusCode, Object result, int status,
                                                                     String message) {
        try {
            JSONObject response = new JSONObject();
            response.put("code", statusCode.value());
            if (result != null) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(Include.NON_NULL);
                String jsonString = mapper.writeValueAsString(result);
                response.put("result", new JSONArray(jsonString));
            }

            response.put("status", status);
            response.put("message", message);

            return ResponseEntity.status(statusCode).body(new InputStreamResource(new ByteArrayInputStream(response.toString().getBytes())));
        } catch (Throwable e) {
            e.printStackTrace();
            JSONObject response = new JSONObject();
            response.put("code", statusCode);
            response.put("status", status);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InputStreamResource(new ByteArrayInputStream(response.toString().getBytes())));
        }
    }

    public static Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date addDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static PoolingHttpClientConnectionManager createHttpConnManager() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(10);
        connManager.setDefaultMaxPerRoute(10);

        return connManager;
    }

    public static ConnectionKeepAliveStrategy createKeepAliveStrategy() {
        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return Duration.ofSeconds(30).toMillis();
            }
        };
        return myStrategy;
    }

    public static RequestConfig createRequestConfig(int timeout) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();

        return config;
    }

    public static HttpRequestRetryHandler createRequestRetryHandler(int tries) {
        HttpRequestRetryHandler myHandler = (exception, executionCount, httpContext) -> executionCount < tries;
        return myHandler;
    }

    public static ServiceUnavailableRetryStrategy createServiceUnavailableRetryStrategy(int tries,long waitTime) {
        ServiceUnavailableRetryStrategy myStrategy = new ServiceUnavailableRetryStrategy(){

            @Override
            public boolean retryRequest(HttpResponse httpResponse, int executionCount, HttpContext httpContext) {
                return executionCount <= tries &&
                        httpResponse.getStatusLine().getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_SERVICE_UNAVAILABLE;
            }

            @Override
            public long getRetryInterval() {
                return waitTime;
            }
        };
        return myStrategy;
    }

    public static HttpRoutePlanner createRoutePlanner(){
        HttpHost proxyhost = new HttpHost(Settings.getInstance().PROXY_HOST, Settings.getInstance().PROXY_PORT, Settings.getInstance().PROXY_SCHEME);
        return new DefaultProxyRoutePlanner(proxyhost);
    }

    public static String formatMediaSource(String attachMediaID, String postID) {
        return String.format("%s_%s", postID, attachMediaID);
    }

    public static <T> T deepCopy(Object value, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(value), clazz);
    }

    public static <T> T deepCopy(Object value, TypeReference<T> type) throws IOException {
        return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(value), type);
    }

    public static <T> T deepCopy(Object value, JavaType type) throws IOException {
        return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(value), type);
    }
}
