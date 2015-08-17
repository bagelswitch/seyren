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
package com.seyren.core.service.checker.support;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.google.common.base.Optional;
import com.seyren.core.service.checker.TargetChecker;

public abstract class TargetCheckerSupport implements TargetChecker {

    @Inject
    private AutowireCapableBeanFactory applicationContext;

    @Override
    public Map<String, Optional<BigDecimal>> check(Context context) throws Exception {
        CheckTemplate template = getTemplate(context);
        Map<String, Optional<BigDecimal>> r = template.apply();
        return r;
    }

    protected final CheckTemplate getTemplate(Context context) throws IOException {
        CheckTemplate r = retrieveTemplate(context);

        r.setCheck(context.getCheck());
        r.setNow(context.getNow());
        applicationContext.autowireBean(r);

        initTemplate(r);

        return r;
    }

    protected abstract CheckTemplate retrieveTemplate(Context context) throws IOException;

    protected void initTemplate(CheckTemplate r) throws IOException {
    }
}
