package com.example.data_ingestion_service.config;

import com.example.data_ingestion_service.client.ApiClient;
import com.example.data_ingestion_service.exception.CustomRestClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
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
                .defaultHeaders(this::configureHeaders)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    String errorMessage = String.format(
                            "Server error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()
                    );
                    log.error(errorMessage);
                    throw new CustomRestClientException(errorMessage);
                })
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String errorMessage = String.format(
                            "Client error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()
                    );
                    log.error(errorMessage);
                    throw new CustomRestClientException(errorMessage);
                })
                .build();
    }

    private void configureHeaders(HttpHeaders headers) {
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
    }

    @Bean
    public ApiClient apiClient(RestClient restClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                        .build();
        return httpServiceProxyFactory.createClient(ApiClient.class);
    }
}