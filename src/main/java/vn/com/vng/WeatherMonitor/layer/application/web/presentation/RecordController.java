package vn.com.vng.WeatherMonitor.layer.application.web.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.QuerySearch;
import vn.com.vng.WeatherMonitor.layer.application.model.RecordRespondPayload;
import vn.com.vng.WeatherMonitor.layer.application.service.RecordService;
import vn.com.vng.WeatherMonitor.ultility.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("record")
@RestController
public class RecordController {
    @Autowired
    RecordService recordService;

    @GetMapping(path = "query", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> listCommentsByMediaID(@RequestParam(value = "region", required = false) String regionCode,
                                                        @RequestParam(value = "area", required = false) String areaCode,
                                                        @RequestParam(value = "from", required = false) Long fromDate,
                                                        @RequestParam(value = "to", required = false) Long toDate,
                                                        HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
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
            return Util.formatResponse(HttpStatus.OK, result, 1, "List records successfully!");
        }catch (InvalidDataException e) {
            return Util.formatResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, 0, e.getMessage());
        } catch (Throwable e) {
            String message = "An error has occurred when listing weather record";
            e.printStackTrace();
            return Util.formatResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, 0, message);
        }
    }
}
