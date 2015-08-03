package com.seyren.core.service.checker.elasticsearch.template;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.searchbox.core.SearchResult;

/**
 * A simple "count" aggregation that is basically a number of hits the query returned.
 */
public class Count extends Base {

    @Override
    protected Map<String, Optional<BigDecimal>> extractMetrics(SearchResult sr) {
        return ImmutableMap.of(getCheck().getName(), Optional.of(BigDecimal.valueOf(sr.getTotal())));
    }
}
