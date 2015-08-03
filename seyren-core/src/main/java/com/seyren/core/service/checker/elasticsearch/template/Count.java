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
