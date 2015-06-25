package com.seyren.core.service.checker;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.seyren.core.domain.Check;
import com.seyren.core.util.influxdb.InfluxDbClient;
import com.seyren.core.util.influxdb.QueryResponse;

@Named
public class InfluxDbTargetChecker implements TargetChecker {

    private final InfluxDbClient client;

    @Inject
    public InfluxDbTargetChecker(InfluxDbClient client) {
        this.client = client;
    }

    @Override
    public boolean canHandle(Check check) {
        return check.getType() == Check.Type.INFLUX_DB;
    }

    @Override
    public Map<String, Optional<BigDecimal>> check(Check check) throws Exception {
        Map<String, Optional<BigDecimal>> r = new HashMap<String, Optional<BigDecimal>>();
        QueryResponse qr = client.query(check.getTarget());
        for (QueryResponse.Result result: qr.getResults()) {
            for (QueryResponse.Series series: result.getSeries()) {
                r.put(series.getName(), Optional.fromNullable(getLatestValue(series)));
            }
        }
        return r;
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
