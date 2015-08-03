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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Optional;
import com.seyren.core.domain.Check;

@Named("multiTypeTargetChecker")
public class MultiTypeTargetChecker implements TargetChecker {

    private List<TargetChecker> targetCheckers;

    @Inject
    public MultiTypeTargetChecker(List<TargetChecker> targetCheckers) {
        this.targetCheckers = targetCheckers;
    }

    @Override
    public boolean canHandle(Check check) {
        return findCheckerFor(check) != null;
    }

    public TargetChecker findCheckerFor(Check check) {
        for (TargetChecker checker: targetCheckers) {
            if (checker.canHandle(check)) {
                return checker;
            }
        }
        return null;
    }

    @Override
    public Map<String, Optional<BigDecimal>> check(Context context) throws Exception {
        TargetChecker checker = findCheckerFor(context.getCheck());
        if (checker == null) {
            throw new IllegalArgumentException("No checkers found for the check: " + context.getCheck());
        }
        return checker.check(context);
    }
}
