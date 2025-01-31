package com.example.data_processing_service.config;

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

    private static final String BASE_URL = "https://localhost:8080/api/v1/markets";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeaders(this::configureHeaders)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    String errorMessage = String.format(
                            "Server error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()
                    );
                    log.error(String.format(
                            "Server error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()));
                    throw new RestClientException(errorMessage);
                })
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String errorMessage = String.format(
                            "Client error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()
                    );
                    log.error(errorMessage);
                    throw new RestClientException(errorMessage);
                })
                .build();
    }

    public void configureHeaders(HttpHeaders headers) {
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "deflate");
    }
}
