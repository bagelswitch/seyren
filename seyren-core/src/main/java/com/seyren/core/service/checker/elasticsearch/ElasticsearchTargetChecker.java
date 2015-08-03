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
package com.seyren.core.service.checker.elasticsearch;

import javax.inject.Inject;
import javax.inject.Named;

import com.seyren.core.domain.Check;
import com.seyren.core.service.checker.elasticsearch.template.Base;
import com.seyren.core.service.checker.elasticsearch.template.Count;
import com.seyren.core.service.checker.elasticsearch.template.Elapsed;
import com.seyren.core.service.checker.elasticsearch.template.Mean;
import com.seyren.core.service.checker.support.CheckTemplate;
import com.seyren.core.service.checker.support.YamlTargetCheckerSupport;
import com.seyren.core.util.config.SeyrenConfig;

@Named
public class ElasticsearchTargetChecker extends YamlTargetCheckerSupport {

    private String timestampField;

    @Inject
    public ElasticsearchTargetChecker(SeyrenConfig config) {
        this.timestampField = config.getElasticsearchTimestampField();

        registerTemplate("elapsed", Elapsed.class);
        registerTemplate("count", Count.class);
        registerTemplate("mean", Mean.class);
    }

    @Override
    public boolean canHandle(Check check) {
        return check.getType() == Check.Type.ELASTICSEARCH;
    }

    @Override
    protected void initTemplate(CheckTemplate t) {
        Base b = (Base) t;
        b.setTimestampField(timestampField);
    }
}
