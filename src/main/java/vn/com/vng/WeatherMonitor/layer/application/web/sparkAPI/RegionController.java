package vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import spark.Request;
import spark.RouteGroup;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.RegionResponsePayload;
import vn.com.vng.WeatherMonitor.layer.application.service.RegionService;
import vn.com.vng.WeatherMonitor.ultility.Util;

import java.util.List;

import static spark.Spark.*;

@Component
public class RegionController implements RouteGroup {

    @Autowired
    RegionService regionService;

    public Object listRegions(Request req, spark.Response res) {
        try {
            String token = req.headers("token");
            if(token == null) {
                throw new InvalidDataException("header[token] required");
            }
            if(!token.equals(Settings.getInstance().TOKEN)) {
                throw new InvalidDataException("header[token] is invalid");
            }
            List<RegionResponsePayload> result = regionService.listRegion();
            ResponseEntity<String> response = Util.formatResponse(HttpStatus.OK, result, 1, "List comments by media successfully!");
            res.body(response.getBody());
            res.status(response.getStatusCodeValue());
        } catch (Throwable e) {
            String message = "An error has occurred when listing regions";
            e.printStackTrace();
            ResponseEntity<String> response = Util.formatResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, 0, message);
            res.body(response.getBody());
            res.status(response.getStatusCodeValue());
        }
        return null;
    }
    @Override
    public void addRoutes() {
        get("/list", this::listRegions);

        after((req, res) -> {
            res.type("application/json");
        });

    }
}
