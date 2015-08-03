package com.seyren.core.service.checker.support;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.Instant;
import org.joda.time.Period;

import com.google.common.base.Strings;
import com.seyren.core.domain.Check;
import com.seyren.core.util.datetime.PeriodFormats;

public abstract class CheckTemplateSupport implements CheckTemplate {

    private String name;
    private Check check;

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = checkNotNull(name);
    }

    public Check getCheck() {
        return check;
    }

    @Override
    public void setCheck(Check check) {
        this.check = checkNotNull(check);
    }

    protected Instant getCheckFrom() {
        String s = Strings.nullToEmpty(getCheck().getFrom()).trim();
        if (s.isEmpty()) {
            s = "-11m";
        }
        return toInstant(s);
    }

    protected Instant getCheckUntil() {
        String s = Strings.nullToEmpty(getCheck().getUntil()).trim();
        if (s.isEmpty()) {
            s = "-1m";
        }
        return toInstant(s);
    }

    protected Instant toInstant(String s) {
        s = s.trim();
        if (s.startsWith("-")) {
            Period period = PeriodFormats.DEFAULT.parsePeriod(s);
            return Instant.now().plus(period.toStandardDuration());
        } else {
            throw new IllegalArgumentException("The relative datetime must start with a minus sign ('-')");
        }
    }
}
