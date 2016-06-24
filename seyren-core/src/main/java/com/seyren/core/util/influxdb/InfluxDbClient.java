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
package com.seyren.core.util.influxdb;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.client.AuthCache;
import org.apache.http.impl.auth.BasicScheme;

import com.seyren.core.util.config.SeyrenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class InfluxDbClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbClient.class);

    private static final int DEFAULT_MAX_PER_ROUTE = 50;

    private final URI queryUri;
    private final String username;
    private final String password;
    private final String database;
    private final String proxyuser;
    private final String proxypass;
    private final HttpClient httpClient;

    private static final ObjectMapperResponseHandler<QueryResponse> QUERY_RESPONSE_HANDLER =
            new ObjectMapperResponseHandler<QueryResponse>(QueryResponse.class);

    @Inject
    public InfluxDbClient(SeyrenConfig config) {
        queryUri = URI.create(config.getInfluxDbUrl() + "/query");
        username = config.getInfluxDbUsername();
        password = config.getInfluxDbPassword();
        database = config.getInfluxDbDatabase();
        proxyuser = config.getInfluxDbProxyuser();
        proxypass = config.getInfluxDbProxypass();
        httpClient = createHttpClient(config);
    }

    public QueryResponse query(String query) {
        URI uri;
        try {
            uri = new URIBuilder(queryUri)
                    .addParameter("u", username)
                    .addParameter("p", password)
                    .addParameter("db", database)
                    .addParameter("q", checkNotNull(query, "'query' must not be null"))
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

        HttpGet get = new HttpGet(uri);
        QueryResponse r;
        try {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxyuser, proxypass);
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            AuthCache authCache = new BasicAuthCache();
            authCache.put(targetHost, new BasicScheme());
            final HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(provider);
            context.setAuthCache(authCache);

            r = httpClient.execute(get, QUERY_RESPONSE_HANDLER, context);

            //LOGGER.error(uri.toString());
            //LOGGER.error(r.getMessage());
        } catch (IOException e) {
            throw new InfluxDbReadException("Failed to read from InfluxDB: " + e.getMessage(), e);
        } finally {
            get.reset();
        }

        String error = r.findFirstError();
        if (error != null) {
            throw new InfluxDbQueryException(r, StringUtils.capitalize(error));
        }
        return r;
    }

    private HttpClient createHttpClient(SeyrenConfig config) {

        HttpClient r = HttpClientBuilder.create()
                .setConnectionManager(createConnectionManager(config))
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(config.getInfluxDbConnectionRequestTimeout())
                        .setConnectTimeout(config.getInfluxDbConnectTimeout())
                        .setSocketTimeout(config.getInfluxDbSocketTimeout())
                        .build())
                .build();
        return r;
    }

    private HttpClientConnectionManager createConnectionManager(SeyrenConfig config) {
        PoolingHttpClientConnectionManager r = new PoolingHttpClientConnectionManager();
        r.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        return r;
    }
}
