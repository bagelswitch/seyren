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
import static com.google.common.collect.ImmutableMap.Builder;
import static com.google.common.collect.ImmutableMap.builder;
import static com.google.common.collect.ImmutableMap.of;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

class QueryBuilder {

    private String luceneQuery;
    private String[] timeRange;
    private Map<String, ?> agg;
    private final Map<String, Boolean> sort = new LinkedHashMap<>();
    private final Set<String> include = new LinkedHashSet<>();
    private int size;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public QueryBuilder luceneQuery(String luceneQuery) {
        this.luceneQuery = luceneQuery;
        return this;
    }

    public QueryBuilder timeRange(String field, String from, String to) {
        if (from != null || to != null) {
            timeRange = new String[]{ checkNotNull(field), from, to };
        } else {
            timeRange = null;
        }
        return this;
    }

    public QueryBuilder timeRange(String field, ReadableInstant from , ReadableInstant to) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();
        return timeRange(field, from != null ? formatter.print(from) : null,
                to != null ? formatter.print(to) : null);
    }

    public QueryBuilder clearTimeRange() {
        return timeRange(null, (String) null, null);
    }

    public QueryBuilder agg(Map<String, ?> agg) {
        this.agg = agg;
        return this;
    }

    public QueryBuilder sort(String field, boolean asc) {
        sort.put(checkNotNull(field), asc);
        return this;
    }

    public QueryBuilder include(String field) {
        include.add(checkNotNull(field));
        return this;
    }

    public QueryBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString() {
        Builder<Object, Object> json = ImmutableMap.builder();
        if (luceneQuery != null || timeRange != null) {
            Builder<Object, Object> t = ImmutableMap.builder();

            if (luceneQuery != null) {
                t.put("query", of("query_string",
                        of("query", luceneQuery)));
            }
            if (timeRange != null) {
                ImmutableMap.Builder<String, Object> range = builder();
                if (timeRange[1] != null) {
                    range.put("gte", timeRange[1]);
                }
                if (timeRange[2] != null) {
                    range.put("lt", timeRange[2]);
                }
                t.put("filter", of("range", of(timeRange[0], range.build())));
            }

            json.put("query", of("filtered", t.build()));
        }
        if (agg != null) {
            json.put("aggs", of("agg", agg));
        }
        if (!sort.isEmpty()) {
            ImmutableList.Builder<Object> t = ImmutableList.builder();
            for (Map.Entry<String, Boolean> s: sort.entrySet()) {
                t.add(of(s.getKey(), of("order", s.getValue() ? "asc" : "desc")));
            }
            json.put("sort", t.build());
        }
        if (!include.isEmpty()) {
            json.put("_source", include);
        }
        json.put("size", size);

        try {
            return MAPPER.writeValueAsString(json.build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
