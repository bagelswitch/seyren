package com.seyren.core.util.elasticsearch;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.seyren.core.util.config.SeyrenConfig;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

@Named
public class ElasticsearchClient {

    private final JestClientFactory clientFactory;
    private final Supplier<JestClient> client = Suppliers.memoize(new Supplier<JestClient>() {
        @Override
        public JestClient get() {
            return clientFactory.getObject();
        }
    });
    private final String index;

    @Inject
    public ElasticsearchClient(SeyrenConfig c) {
        clientFactory = new JestClientFactory();
        HttpClientConfig.Builder b = new HttpClientConfig.Builder(Splitter.on('|').splitToList(c.getElasticsearchUrl()))
                .multiThreaded(true)
                .defaultCredentials(c.getElasticsearchUsername(), c.getElasticsearchPassword())
                .defaultMaxTotalConnectionPerRoute(50)
                .connTimeout(c.getElasticsearchConnectTimeout())
                .readTimeout(c.getElasticsearchSocketTimeout());
        clientFactory.setHttpClientConfig(b.build());
        index = c.getElasticsearchIndex();
    }

    public SearchResult query(String query) throws IOException {
        SearchResult r = client.get().execute(new Search.Builder(query)
            .addIndex(index)
            .build());
        return r;
    }

    public SearchResult querySimple(String luceneQuery, String from, String to, int limit) throws IOException {
        String q = "{" +
                "    \"query\": {" +
                "        \"filtered\": {" +
                "            \"query\": {" +
                "                \"bool\": {" +
                "                    \"should\": [{" +
                "                        \"query_string\": {" +
                "                            \"query\": \"${luceneQuery}\"" +
                "                        }" +
                "                    }]" +
                "                }" +
                "            }," +
                "            \"filter\": {" +
                "                \"bool\": {" +
                "                    \"must\": [{" +
                "                        \"range\": {" +
                "                            \"@timestamp\": {" +
                "                                \"from\": \"${from}\"," +
                "                                \"to\": \"${to}\"" +
                "                            }" +
                "                        }" +
                "                    }]" +
                "                }" +
                "            }" +
                "        }" +
                "    }," +
                "    \"size\": ${limit}" +
                "}";
        q = StringUtils.replaceEach(q, new String[]{
                        "${luceneQuery}",
                        "${from}",
                        "${to}",
                        "${limit}" },
                new String[]{
                        StringEscapeUtils.escapeJson(luceneQuery),
                        StringEscapeUtils.escapeJson(from),
                        StringEscapeUtils.escapeJson(to),
                        String.valueOf(limit) });
        return query(q);
    }
}
