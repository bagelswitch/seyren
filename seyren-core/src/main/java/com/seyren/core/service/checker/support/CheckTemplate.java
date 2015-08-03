package com.seyren.core.service.checker.support;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.seyren.core.domain.Check;

public interface CheckTemplate {

    @JsonIgnore
    void setName(String name);

    @JsonIgnore
    void setCheck(Check check);

    @JsonIgnore
    void setNow(Instant now);

    Map<String, Optional<BigDecimal>> apply() throws IOException;
}
