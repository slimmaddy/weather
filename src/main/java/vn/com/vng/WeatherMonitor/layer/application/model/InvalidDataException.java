package vn.com.vng.WeatherMonitor.layer.application.model;

public class InvalidDataException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidDataException(String message) {
        super(message);
    }

}
