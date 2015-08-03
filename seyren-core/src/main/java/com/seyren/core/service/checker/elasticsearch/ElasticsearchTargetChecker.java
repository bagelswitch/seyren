package com.seyren.core.service.checker.elasticsearch;

import javax.inject.Inject;
import javax.inject.Named;

import com.seyren.core.domain.Check;
import com.seyren.core.service.checker.elasticsearch.template.Mean;
import com.seyren.core.service.checker.elasticsearch.template.Base;
import com.seyren.core.service.checker.elasticsearch.template.Count;
import com.seyren.core.service.checker.elasticsearch.template.Elapsed;
import com.seyren.core.service.checker.support.CheckTemplate;
import com.seyren.core.service.checker.support.YamlTargetCheckerSupport;
import com.seyren.core.util.config.SeyrenConfig;
import com.seyren.core.util.elasticsearch.ElasticsearchClient;

@Named
public class ElasticsearchTargetChecker extends YamlTargetCheckerSupport {

    private final ElasticsearchClient client;
    private String timestampField;

    @Inject
    public ElasticsearchTargetChecker(ElasticsearchClient client, SeyrenConfig config) {
        this.client = client;
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
