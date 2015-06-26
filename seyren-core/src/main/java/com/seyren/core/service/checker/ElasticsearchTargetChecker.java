package com.seyren.core.service.checker;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.seyren.core.domain.Check;
import com.seyren.core.util.elasticsearch.ElasticsearchClient;
import io.searchbox.core.SearchResult;

@Named
public class ElasticsearchTargetChecker implements TargetChecker {

    private final ElasticsearchClient client;

    @Inject
    public ElasticsearchTargetChecker(ElasticsearchClient client) {
        this.client = client;
    }

    @Override
    public boolean canHandle(Check check) {
        return check.getType() == Check.Type.ELASTICSEARCH;
    }

    @Override
    public Map<String, Optional<BigDecimal>> check(Check check) throws Exception {
        String from = "now" + Objects.toString(check.getFrom(), "-1h");
        String to = "now" + Objects.toString(check.getUntil(), "");
        SearchResult sr = client.querySimple(check.getTarget(), from, to, 10);
        Integer total = sr.getTotal();
        return ImmutableMap.of(check.getName(), Optional.of(BigDecimal.valueOf(total)));
    }
}
