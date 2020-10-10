package vn.com.vng.WeatherMonitor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
public class MyCustomRequestInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static String makeUrl(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(request.getRequestURL().toString());
        if (request.getQueryString() != null && !request.getQueryString().trim().isEmpty()) {
            builder.append("?").append(request.getQueryString());
        }
        return builder.toString();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = Instant.now().toEpochMilli();
        System.out.println("\nRequest URL:: " + makeUrl(request) + " :: Start Time=" + Instant.now() + "\n");
        request.setAttribute("startTime", startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long stopTime = Instant.now().toEpochMilli();
        System.out.println("\nRequest URL:: " + makeUrl(request) + " :: Time Taken="
                + (stopTime - startTime) + "\n");
        if((stopTime - startTime) > 1000) {
            logger.error("\nSlow request URL:: " + makeUrl(request) + " :: Time Taken="
                    + (stopTime - startTime) + "\n");
        }
    }
}
