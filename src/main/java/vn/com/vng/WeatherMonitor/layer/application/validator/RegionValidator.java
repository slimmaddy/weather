package vn.com.vng.WeatherMonitor.layer.application.validator;

import vn.com.vng.WeatherMonitor.layer.application.entity.Region;
import vn.com.vng.WeatherMonitor.layer.application.model.InvalidDataException;

public class RegionValidator {
    public static void validate(Region region) throws InvalidDataException {
        if(region.getCode() == null || region.getCode().trim().isEmpty()) {
            throw new InvalidDataException("field [code] missing");
        }
        if(region.getLatitude() == null ) {
            throw new InvalidDataException("field [latitude] missing");
        }
        if(region.getLongitude() == null) {
            throw new InvalidDataException("field [longitude] missing");
        }
        if(region.getZoom() == null) {
            throw new InvalidDataException("field [zoom] missing");
        }
    }
}
