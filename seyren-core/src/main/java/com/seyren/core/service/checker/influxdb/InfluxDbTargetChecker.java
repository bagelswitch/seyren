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
