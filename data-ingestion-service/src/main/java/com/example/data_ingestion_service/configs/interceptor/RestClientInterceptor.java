package com.example.data_ingestion_service.configs.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution)
            throws IOException {
        // Log the request details
        logRequestDetailsInfo(request, body);

        // Proceed with the request execution
        ClientHttpResponse response = execution.execute(request, body);

        // Log the response details
        logResponseDetails(response);

        return response;
    }

    private void logRequestDetailsInfo(HttpRequest request, byte[] body) throws IOException {
        log.info("Request URI: {}", request.getURI());
        log.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponseDetails(ClientHttpResponse response) throws IOException {
        log.info("Response Status Code: {}", response.getStatusCode());
        log.info("Response Headers: {}", response.getHeaders());
        // Read the response body (if needed)
        String responseBody = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        log.info("Response Body: {}", responseBody);
    }
}
