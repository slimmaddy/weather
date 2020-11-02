package vn.com.vng.WeatherMonitor.layer.application.web.sparkAPI;

import org.apache.commons.httpclient.HttpStatus;
import spark.*;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.QuerySearch;
import vn.com.vng.WeatherMonitor.layer.application.model.RecordRespondPayload;
import vn.com.vng.WeatherMonitor.layer.application.service.RecordService;
import vn.com.vng.WeatherMonitor.ultility.Util;

import java.util.List;

import static spark.Spark.after;
import static spark.Spark.get;

public class RecordController implements RouteGroup {

    RecordService recordService;

    public RecordController() {
        recordService = new RecordService();
    }

    public Object listRecords(Request req, spark.Response res) {
        try {
            String regionCode = req.queryParams("region");
            String areaCode = req.queryParams("area");
            Long fromDate = Long.parseLong(req.queryParams("from"));
            Long toDate = Long.parseLong(req.queryParams("to"));

            String token = req.headers("token");
            if(token == null) {
                throw new InvalidDataException("header[token] required");
            }
            if(!token.equals(Settings.getInstance().TOKEN)) {
                throw new InvalidDataException("header[token] is invalid");
            }
            QuerySearch querySearch = new QuerySearch();
            querySearch.setRegionCode(regionCode);
            querySearch.setFromDate(fromDate);
            querySearch.setToDate(toDate);
            querySearch.setAreaCode(areaCode);

            List<RecordRespondPayload> result = recordService.listRecord(querySearch);
            return Util.formatResponse(HttpStatus.SC_OK, result, 1, "List records successfully!");
        }catch (InvalidDataException e) {
            return Util.formatResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, null, 0, e.getMessage());
        } catch (Throwable e) {
            String message = "An error has occurred when listing weather record";
            e.printStackTrace();
            return Util.formatResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, null, 0, message);
        }
    }

    @Override
    public void addRoutes() {
        get("/query", this::listRecords);

        after((req, res) -> {
            res.type("application/json");
        });

    }
}
