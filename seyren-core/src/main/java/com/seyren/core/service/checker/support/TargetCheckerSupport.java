package com.seyren.core.service.checker.support;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.google.common.base.Optional;
import com.seyren.core.domain.Check;
import com.seyren.core.service.checker.TargetChecker;

public abstract class TargetCheckerSupport implements TargetChecker {

    @Inject
    private AutowireCapableBeanFactory applicationContext;

    @Override
    public Map<String, Optional<BigDecimal>> check(Check check) throws Exception {
        return getTemplate(check).apply();
    }

    protected final CheckTemplate getTemplate(Check check) throws IOException {
        CheckTemplate r = retrieveTemplate(check);

        r.setCheck(check);
        applicationContext.autowireBean(r);

        initTemplate(r);

        return r;
    }

    protected abstract CheckTemplate retrieveTemplate(Check check) throws IOException;

    protected void initTemplate(CheckTemplate r) throws IOException {
    }
}
