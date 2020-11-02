package vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI;

import org.apache.commons.httpclient.HttpStatus;
import spark.Request;
import spark.RouteGroup;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.RegionResponsePayload;
import vn.com.vng.WeatherMonitor.layer.application.service.RegionService;
import vn.com.vng.WeatherMonitor.ultility.Util;

import java.util.List;

import static spark.Spark.*;

public class RegionController implements RouteGroup {

    RegionService regionService;

    public RegionController() {
        this.regionService = new RegionService();
    }

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
            return Util.formatResponse(HttpStatus.SC_OK, result, 1, "List comments by media successfully!");

        } catch (Throwable e) {
            String message = "An error has occurred when listing regions";
            e.printStackTrace();
            return Util.formatResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, message, 0, message);
        }
    }
    @Override
    public void addRoutes() {
        get("/list", this::listRegions);

        after((req, res) -> {
            res.type("application/json");
        });

    }
}
