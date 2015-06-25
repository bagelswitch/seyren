package com.seyren.core.util.influxdb;

public class InfluxDbQueryException extends InfluxDbException {

    private final QueryResponse queryResponse;

    public InfluxDbQueryException(QueryResponse queryResponse, String message) {
        super(message);
        this.queryResponse = queryResponse;
    }

    public InfluxDbQueryException(QueryResponse queryResponse, String message, Throwable cause) {
        super(message, cause);
        this.queryResponse = queryResponse;
    }

    public InfluxDbQueryException(QueryResponse queryResponse, Throwable cause) {
        super(cause);
        this.queryResponse = queryResponse;
    }
}
