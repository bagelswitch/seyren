package com.seyren.core.util.influxdb;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryResponse {

    public static class Result {

        private final String error;
        private final List<Series> series;

        @JsonCreator
        public Result(@JsonProperty("error") String error,
                      @JsonProperty("series") List<Series> series) {
            this.error = error;
            this.series = series != null ? Collections.unmodifiableList(series) : Collections.<Series>emptyList();
        }

        public String getError() {
            return error;
        }

        public List<Series> getSeries() {
            return series;
        }
    }

    public static class Series {

        private final String name;
        private final Map<String, Object> tags;
        private final List<String> columns;
        private final List<Row> rows;

        @JsonCreator
        public Series(@JsonProperty("name") String name,
                      @JsonProperty("tags") Map<String, Object> tags,
                      @JsonProperty("columns") List<String> columns,
                      @JsonProperty("values") List<Row> rows) {
            this.name = name;
            this.tags = tags != null ? Collections.unmodifiableMap(tags) : Collections.<String, Object>emptyMap();
            this.columns = columns != null ? Collections.unmodifiableList(columns) : Collections.<String>emptyList();
            this.rows = rows != null ? Collections.unmodifiableList(rows) : Collections.<Row>emptyList();
        }

        public String getName() {
            return name;
        }

        public Map<String, Object> getTags() {
            return tags;
        }

        public List<String> getColumns() {
            return columns;
        }

        public List<Row> getRows() {
            return rows;
        }
    }

    public static class Row {

        private final Instant timestamp;
        private final List<BigDecimal> values;

        @JsonCreator
        public Row(List<Object> row) {
            checkNotNull(row);

            checkElementIndex(0, row.size());
            Object timestampStr = row.get(0);
            checkArgument(timestampStr instanceof String);
            this.timestamp = ISODateTimeFormat.dateTimeParser().parseDateTime((String) timestampStr).toInstant();

            if (row.size() == 1) {
                values = Collections.emptyList();
                return;
            }

            values = new ArrayList<BigDecimal>(row.size() - 1);
            for (int i = 1; i < row.size(); i++) {
                Object value = row.get(i);
                BigDecimal v;
                if (value instanceof Integer) {
                    v = BigDecimal.valueOf((Integer) value);
                } else if (value instanceof Long) {
                    v = BigDecimal.valueOf((Long) value);
                } else if (value == null || value instanceof BigDecimal) {
                    v = (BigDecimal) value;
                } else {
                    throw new IllegalArgumentException("Expected Integer, Long, BigDecimal or null but found: " + value.getClass());
                }
                values.add(v);
            }
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public List<BigDecimal> getValues() {
            return values;
        }
    }

    private final List<Result> results;
    private final String error;

    @JsonCreator
    public QueryResponse(@JsonProperty("results") List<Result> results,
                         @JsonProperty("error") String error) {
        this.results = results;
        this.error = error;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getError() {
        return error;
    }

    public String findFirstError() {
        if (error != null) {
            return error;
        }
        if (results == null) {
            return null;
        }
        for (QueryResponse.Result result: results) {
            if (result.getError() != null) {
                return result.getError();
            }
        }
        return null;
    }
}
