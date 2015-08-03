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
package com.seyren.core.util.elasticsearch;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

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

    public SearchResult search(String query) throws IOException {
        SearchResult r = client.get().execute(new Search.Builder(query)
            .addIndex(index)
            .build());
        if (!r.isSucceeded()) {
            throw new ElasticsearchApiException(r.getErrorMessage());
        }
        return r;
    }
}
