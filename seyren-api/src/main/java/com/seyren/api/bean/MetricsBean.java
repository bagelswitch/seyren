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
package com.seyren.api.bean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.seyren.api.jaxrs.MetricsResource;
import com.seyren.core.domain.Check;
import com.seyren.core.service.checker.TargetChecker;

@Named
public class MetricsBean implements MetricsResource {

    private final TargetChecker targetChecker;

    @Inject
    public MetricsBean(@Named("multiTypeTargetChecker") TargetChecker targetChecker) {
        this.targetChecker = targetChecker;
    }

    @Override
    public Response totalMetric(@QueryParam("type") Check.Type type,
                                @QueryParam("target") String target) {
        try {
            Map<String, Optional<BigDecimal>> targetValues = targetChecker.check(new TargetChecker.Context(
                    new Check().withType(type).withTarget(target).withName(target), Instant.now()));
            Map<String, Integer> result = new HashMap<String, Integer>();
            result.put(target, targetValues.size());
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
