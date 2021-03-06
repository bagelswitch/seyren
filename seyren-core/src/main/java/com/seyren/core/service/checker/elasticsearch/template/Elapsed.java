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
import static com.google.common.collect.ImmutableMap.of;

import java.math.BigDecimal;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import io.searchbox.core.SearchResult;

/**
 * Calculates the number {@link #setTimeUnit(TimeUnit) timeUnitS} passed since the last event matched by the
 * {@link #setQuery(String) query}. Note that the time range has no effect for this template.
 */
public class Elapsed extends Base {

    public enum TimeUnit {
        second, minute, hour, day
    }

    private TimeUnit timeUnit = TimeUnit.minute;

    @Override
    protected QueryBuilder buildQuery(QueryBuilder qb) {
        return super.buildQuery(qb)
                .clearTimeRange()
                .include(getTimestampField())
                .sort(getTimestampField(), false)
                .size(1);
    }

    @Override
    protected Map<String, Optional<BigDecimal>> extractMetrics(SearchResult sr) {
        BigDecimal r;

        JsonArray hits = sr.getJsonObject()
                .getAsJsonObject("hits")
                .getAsJsonArray("hits");
        if (hits.size() != 0) {
            String timestampStr = hits.get(0).getAsJsonObject().getAsJsonObject("_source").get("@timestamp").getAsString();
            DateTime timestamp = ISODateTimeFormat.dateTime().parseDateTime(timestampStr);
            long elapsed = getNow().getMillis() - timestamp.getMillis();
            switch (timeUnit) {
                case day:       elapsed /= 24;
                case hour:      elapsed /= 60;
                case minute:    elapsed /= 60;
                case second:    elapsed /= 1000;
                    break;
                default:
                    throw new AssertionError("Unhandled TimeUnit: " + timeUnit);
            }
            r = BigDecimal.valueOf(elapsed);
        } else {
            r = null;
        }
        return of(getCheck().getName(), Optional.fromNullable(r));
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = checkNotNull(timeUnit);
    }
}
