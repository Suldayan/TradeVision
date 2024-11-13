package com.example.data_ingestion_service.config;

import com.example.data_ingestion_service.client.ApiClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${coincap.base-url}")
    private String baseUrl;

    @Value("${coincap.api-key}")
    private String apiKey;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
                    headers.add(HttpHeaders.ACCEPT, "application/json");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
                    headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
                })
                .build();
    }

    @SneakyThrows
    @Bean
    public ApiClient apiClient(RestClient restClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                        .build();
        return httpServiceProxyFactory.createClient(ApiClient.class);
    }
}