package vn.com.vng.WeatherMonitor.layer.application.web.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.vng.WeatherMonitor.config.Settings;
import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;
import vn.com.vng.WeatherMonitor.layer.application.model.RegionResponsePayload;
import vn.com.vng.WeatherMonitor.layer.application.service.RegionService;
import vn.com.vng.WeatherMonitor.ultility.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("region")
@RestController
public class RegionController {
    @Autowired
    RegionService regionService;

    @PostMapping(path = "create", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> createComments(@RequestBody Region region,
                                                 HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            if(token == null) {
                throw new InvalidDataException("header[token] required");
            }
            if(!token.equals(Settings.getInstance().TOKEN)) {
                throw new InvalidDataException("header[token] is invalid");
            }
            Region result = regionService.insertRegion(region);
            return Util.formatResponse(HttpStatus.OK, result, 1, "Result");
        }catch (InvalidDataException e) {
            return Util.formatResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, 0, e.getMessage());
        }
        catch (Throwable e) {
            String message = "An error has occurred when insert region";
            return Util.formatResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, 0, message);
        }
    }

    @GetMapping(path = "list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> listCommentsByMediaID(HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            if(token == null) {
                throw new InvalidDataException("header[token] required");
            }
            if(!token.equals(Settings.getInstance().TOKEN)) {
                throw new InvalidDataException("header[token] is invalid");
            }
            List<RegionResponsePayload> result = regionService.listRegion();
            return Util.formatResponse(HttpStatus.OK, result, 1, "List comments by media successfully!");
        } catch (Throwable e) {
            String message = "An error has occurred when listing regions";
            e.printStackTrace();
            return Util.formatResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, 0, message);
        }
    }
}
