package com.seyren.core.util.elasticsearch;

public class ElasticsearchApiException extends ElasticsearchException {

    public ElasticsearchApiException() {
    }

    public ElasticsearchApiException(String message) {
        super(message);
    }

    public ElasticsearchApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticsearchApiException(Throwable cause) {
        super(cause);
    }
}
