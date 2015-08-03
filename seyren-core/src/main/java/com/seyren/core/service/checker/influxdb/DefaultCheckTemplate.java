package com.seyren.core.service.checker.influxdb;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Optional;
import com.seyren.core.service.checker.support.CheckTemplateSupport;
import com.seyren.core.util.influxdb.InfluxDbClient;
import com.seyren.core.util.influxdb.QueryResponse;

class DefaultCheckTemplate extends CheckTemplateSupport {

    @Inject
    private InfluxDbClient client;

    @Override
    public Map<String, Optional<BigDecimal>> apply() throws IOException {
        Map<String, Optional<BigDecimal>> r = new HashMap<>();
        QueryResponse qr = client.query(prepareQuery(getCheck().getTarget()));
        for (QueryResponse.Result result: qr.getResults()) {
            for (QueryResponse.Series series: result.getSeries()) {
                r.put(series.getName(), Optional.fromNullable(getLatestValue(series)));
            }
        }
        return r;
    }

    private String prepareQuery(String q) {
        DateTimeFormatter f = ISODateTimeFormat.dateTime();
        return q.replace("${timeRange}",
                "(time > '" + f.print(getCheckFrom()) + "' AND time <= '" + f.print(getCheckUntil()) + "')");
    }

    private BigDecimal getLatestValue(QueryResponse.Series series) {
        Instant latest = new Instant(0);
        BigDecimal r = null;
        for (QueryResponse.Row row: series.getRows()) {
            if (row.getValues().isEmpty()) {
                continue;
            }
            BigDecimal value = row.getValues().get(0);
            if (value == null) {
                continue;
            }
            if (row.getTimestamp().isAfter(latest)) {
                latest = row.getTimestamp();
                r = value;
            }
        }
        return r;
    }
}
