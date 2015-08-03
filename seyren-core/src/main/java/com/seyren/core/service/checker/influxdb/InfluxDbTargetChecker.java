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
package com.seyren.core.service.checker.influxdb;

import java.io.IOException;

import javax.inject.Named;

import com.seyren.core.domain.Check;
import com.seyren.core.service.checker.support.CheckTemplate;
import com.seyren.core.service.checker.support.TargetCheckerSupport;

@Named
public class InfluxDbTargetChecker extends TargetCheckerSupport {

    @Override
    public boolean canHandle(Check check) {
        return check.getType() == Check.Type.INFLUX_DB;
    }

    @Override
    protected CheckTemplate retrieveTemplate(Context context) throws IOException {
        return new DefaultCheckTemplate();
    }
}
