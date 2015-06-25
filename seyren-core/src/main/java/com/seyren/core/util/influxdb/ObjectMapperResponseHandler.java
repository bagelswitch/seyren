package com.seyren.core.util.influxdb;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

class ObjectMapperResponseHandler<T> implements ResponseHandler<T> {

    private final Class<T> clazz;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    public ObjectMapperResponseHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T handleResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        try {
            InputStream stream = entity.getContent();
            return OBJECT_MAPPER.readValue(stream, clazz);
        } finally {
            EntityUtils.consume(entity);
        }
    }
}
