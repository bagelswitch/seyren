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
package com.seyren.core.service.checker;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.Map;

import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.seyren.core.domain.Check;

public interface TargetChecker {

    final class Context {

        private final Check check;
        private final Instant now;

        public Context(Check check, Instant now) {
            this.check = checkNotNull(check);
            this.now = checkNotNull(now);
        }

        public Check getCheck() {
            return check;
        }

        public Instant getNow() {
            return now;
        }
    }

    boolean canHandle(Check check);
    Map<String, Optional<BigDecimal>> check(Context context) throws Exception;
    
}
