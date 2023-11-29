package com.chenjie.elasticsearch.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description Elasticsearch账密认证模式的RestHighLevel客户端
 */
@Configuration
@Slf4j
public class ESRestAuthConfig {

    @Value("${spring.elasticSearch.host}")
    private String[] esHost;
    @Value("${spring.elasticSearch.port}")
    private int esPort;
    @Value("${spring.elasticSearch.scheme:http}")
    private String scheme;
    @Value("${spring.elasticSearch.user.name:null}")
    private String esUserName;
    @Value("${spring.elasticSearch.user.password:null}")
    private String esUserPassword;
    @Value("${spring.elasticSearch.auth-enable:false}")
    private Boolean authEnable;

    /**
     * ES空闲连接保持时间，单位s，默认3s
     */
    @Value("${spring.elasticSearch.keepAliveTime:3}")
    private int keepAliveTime;

    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    /**
     * 走认证模式的客户端，注入时需要使用@Qualifier("AuthRestHighLevelClient")进行指定
     *
     * @return
     */
    @Bean("AuthRestHighLevelClient")
    public RestHighLevelClient createAuthRestHighLevelClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUserName, esUserPassword));

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SSLIOSessionStrategy sessionStrategy = new SSLIOSessionStrategy(sc, new NullHostNameVerifier());

        SecuredHttpClientConfigCallback httpClientConfigCallback = new SecuredHttpClientConfigCallback(sessionStrategy, credentialsProvider);
        RestClientBuilder builder = RestClient.builder(new HttpHost(esHost[0], esPort, scheme))
                .setHttpClientConfigCallback(httpClientConfigCallback);
        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }

    public static class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    class SecuredHttpClientConfigCallback implements RestClientBuilder.HttpClientConfigCallback {
        @Nullable
        private final CredentialsProvider credentialsProvider;
        /**
         * The {@link SSLIOSessionStrategy} for all requests to enable SSL / TLS encryption.
         */
        private final SSLIOSessionStrategy sslStrategy;

        /**
         * Create a new {@link SecuredHttpClientConfigCallback}.
         *
         * @param credentialsProvider The credential provider, if a username/password have been supplied
         * @param sslStrategy         The SSL strategy, if SSL / TLS have been supplied
         * @throws NullPointerException if {@code sslStrategy} is {@code null}
         */
        SecuredHttpClientConfigCallback(final SSLIOSessionStrategy sslStrategy,
                                        @Nullable final CredentialsProvider credentialsProvider) {
            this.sslStrategy = Objects.requireNonNull(sslStrategy);
            this.credentialsProvider = credentialsProvider;
        }

        /**
         * Get the {@link CredentialsProvider} that will be added to the HTTP client.
         *
         * @return Can be {@code null}.
         */
        @Nullable
        CredentialsProvider getCredentialsProvider() {
            return credentialsProvider;
        }

        /**
         * Get the {@link SSLIOSessionStrategy} that will be added to the HTTP client.
         *
         * @return Never {@code null}.
         */
        SSLIOSessionStrategy getSSLStrategy() {
            return sslStrategy;
        }

        /**
         * Sets the {@linkplain HttpAsyncClientBuilder#setDefaultCredentialsProvider(CredentialsProvider) credential provider},
         *
         * @param httpClientBuilder The client to configure.
         * @return Always {@code httpClientBuilder}.
         */
        @Override
        public HttpAsyncClientBuilder customizeHttpClient(final HttpAsyncClientBuilder httpClientBuilder) {
            // enable SSL / TLS
            httpClientBuilder.setSSLStrategy(sslStrategy);
            // enable user authentication
            if (credentialsProvider != null) {
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
            httpClientBuilder.setKeepAliveStrategy((response, context) -> TimeUnit.MINUTES.toMillis(keepAliveTime));
            return httpClientBuilder;
        }
    }

    @Bean("noAuthRestHighLevelClient")
    public RestHighLevelClient createNoAuthRestHighLevelClient() {
        return getRestHighLevelClient(esHost, esPort, scheme);
    }

    private static HttpHost[] makeHttpHost(String[] host, int port, String scheme) {
        HttpHost[] hosts = new HttpHost[host.length];

        for (int i = 0; i < hosts.length; ++i) {
            hosts[i] = new HttpHost(host[i], port, scheme);
        }

        log.info("hosts组装成功：[{}]", JSONObject.toJSONString(hosts));
        return hosts;
    }

    private void setMutiConnectConfig(RestClientBuilder restClientBuilder) {
        restClientBuilder.setHttpClientConfigCallback((httpClientBuilder) -> {
            httpClientBuilder.setMaxConnTotal(100);
            httpClientBuilder.setMaxConnPerRoute(100);
            return httpClientBuilder;
        });
    }

    private void setConnectTimeOutConfig(RestClientBuilder restClientBuilder) {
        restClientBuilder.setRequestConfigCallback((requestConfigBuilder) -> {
            requestConfigBuilder.setConnectTimeout(1000);
            requestConfigBuilder.setSocketTimeout(30000);
            requestConfigBuilder.setConnectionRequestTimeout(500);
            return requestConfigBuilder;
        });
    }

    public RestHighLevelClient getRestHighLevelClient(String[] host, int port, String scheme) {
        HttpHost[] hosts = makeHttpHost(host, port, scheme);
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        setConnectTimeOutConfig(restClientBuilder);
        setMutiConnectConfig(restClientBuilder);
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            public void onFailure(Node node) {
                log.error("elasticSearch - failure：[{}]", node.toString());
            }
        }).setHttpClientConfigCallback(config -> config.setKeepAliveStrategy((response, context) -> TimeUnit.MINUTES.toMillis(keepAliveTime)));
        ;
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean("AutoCheckWhetherAuthRestHighLevelClient")
    @Primary
    public RestHighLevelClient createAutoCheckWhetherAuthRestHighLevelClient() {
        if (authEnable) {
            return createAuthRestHighLevelClient();
        } else {
            return createNoAuthRestHighLevelClient();
        }
    }

}
