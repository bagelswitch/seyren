/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seyren.core.service.checker.elasticsearch.template;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.seyren.core.service.checker.support.CheckTemplateSupport;
import com.seyren.core.util.elasticsearch.ElasticsearchClient;
import io.searchbox.core.SearchResult;

public abstract class Base extends CheckTemplateSupport {

    @Inject
    private ElasticsearchClient client;
    private String query;
    private String timestampField;

    protected String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = checkNotNull(query);
    }

    protected String getTimestampField() {
        return timestampField;
    }

    @JsonIgnore
    public void setTimestampField(String timestampField) {
        this.timestampField = checkNotNull(timestampField);
    }

    @Override
    public final Map<String, Optional<BigDecimal>> apply() throws IOException {
        checkState(getCheck() != null);
        checkState(getQuery() != null);
        checkState(client != null);
        checkState(getTimestampField() != null);

        QueryBuilder qb = buildQuery(new QueryBuilder());
        checkNotNull(qb);

        SearchResult sr = search(qb);

        return extractMetrics(sr);
    }

    protected abstract Map<String, Optional<BigDecimal>> extractMetrics(SearchResult sr);

    protected SearchResult search(QueryBuilder qb) throws IOException {
        return client.search(qb.toString());
    }

    protected QueryBuilder buildQuery(QueryBuilder qb) {
        qb.luceneQuery(getQuery());

        Instant from = getCheckFrom();
        Instant to = getCheckUntil();
        qb.timeRange(getTimestampField(), from, to);

        return qb;
    }


}
