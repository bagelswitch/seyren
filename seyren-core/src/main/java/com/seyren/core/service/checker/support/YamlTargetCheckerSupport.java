package com.seyren.core.service.checker.support;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public abstract class YamlTargetCheckerSupport extends TargetCheckerSupport {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private final Map<String, Class<? extends CheckTemplate>> templates = new HashMap<>();

    protected void registerTemplate(String name, Class<? extends CheckTemplate> template) {
        checkNotNull(name);
        checkNotNull(template);
        templates.put(name, template);
    }

    @Override
    protected CheckTemplate retrieveTemplate(Context context) throws IOException {
        ObjectNode o = (ObjectNode) YAML_MAPPER.readTree(context.getCheck().getTarget());
        checkArgument(o.size() == 1);
        Map.Entry<String, JsonNode> e = o.fields().next();
        Class<? extends CheckTemplate> clazz = templates.get(e.getKey());
        checkNotNull(clazz);

        CheckTemplate r = YAML_MAPPER.treeToValue(e.getValue(), clazz);
        r.setName(e.getKey());

        return r;
    }
}
