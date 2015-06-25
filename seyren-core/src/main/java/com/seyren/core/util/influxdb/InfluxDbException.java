package com.seyren.core.util.influxdb;

public abstract class InfluxDbException extends RuntimeException {

    public InfluxDbException(String message) {
        super(message);
    }

    public InfluxDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfluxDbException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
}
