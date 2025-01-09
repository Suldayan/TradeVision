package com.example.data_ingestion_service.configs.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;

@Slf4j
@Component
public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    @NonNull
    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            byte @NonNull [] body,
            ClientHttpRequestExecution execution) throws IOException {
        /*
        String apiKey = Optional.of(System.getenv("CC_API_KEY"))
                .orElseThrow(() -> new IllegalStateException("Api key environment variable received as null"));
        */
        String apiKey = "f4217ec2-4649-4d6a-a87e-f5c6fee44527";
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        return execution.execute(request, body);
    }
}
