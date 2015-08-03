package com.seyren.core.service.checker.elasticsearch.template;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.searchbox.core.SearchResult;

/**
 * Calculates the average of the specified {@link #setField(String) field}'s values for the records matched by the {@link #setQuery(String) query}.
 */
public class Mean extends Base {

    private String field;

    @Override
    protected QueryBuilder buildQuery(QueryBuilder qb) {
        checkNotNull(field);
        return super.buildQuery(qb)
                .agg(ImmutableMap.of("avg", ImmutableMap.of("field", field)));
    }

    @Override
    protected Map<String, Optional<BigDecimal>> extractMetrics(SearchResult sr) {
        Double v = sr.getAggregations().getAvgAggregation("agg").getAvg();
        BigDecimal r = v != null ? BigDecimal.valueOf(v) : null;
        return ImmutableMap.of(getCheck().getName(), Optional.fromNullable(r));
    }

    public void setField(String field) {
        this.field = checkNotNull(field);
    }
}
