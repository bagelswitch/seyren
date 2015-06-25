package com.seyren.core.util.influxdb;

public class InfluxDbReadException extends InfluxDbException {

    public InfluxDbReadException(String message) {
        super(message);
    }

    public InfluxDbReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfluxDbReadException(Throwable cause) {
        super(cause);
    }
}
