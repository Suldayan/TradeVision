package com.example.data_ingestion_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value(("${coincap.rest-api.base_url}"))
    private String baseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public RestClient builder() {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(baseUrl)
                .defaultHeader("Accept-Encoding", "gzip")
                .build();
    }
}
