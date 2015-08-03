package com.seyren.core.util.elasticsearch;

public abstract class ElasticsearchException extends RuntimeException {

    public ElasticsearchException() {
    }

    public ElasticsearchException(String message) {
        super(message);
    }

    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticsearchException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
}
