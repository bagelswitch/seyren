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
        return getTemplate(context).apply();
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
