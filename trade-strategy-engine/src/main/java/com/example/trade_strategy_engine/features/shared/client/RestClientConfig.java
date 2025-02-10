package com.example.trade_strategy_engine.features.shared.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Configuration
public class RestClientConfig {

    // TODO configure this with the right data processing microservice url
    private static final String BASE_URL = "https://localhost:8081";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeaders(this::configureHeaders)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RestClientException(String.format(
                            "Server error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()));
                })
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RestClientException(String.format(
                            "Client error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()));
                })
                .build();
    }

    public void configureHeaders(HttpHeaders headers) {
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "deflate");
    }
}