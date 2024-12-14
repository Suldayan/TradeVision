package com.example.data_ingestion_service.configs;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.clients.ExchangeClient;
import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.configs.exception.RestClientException;
import com.example.data_ingestion_service.configs.interceptor.RestClientInterceptor;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RestClientConfig {
    private final RestClientInterceptor restClientInterceptor;

    @Value("${coincap.base-url}")
    private String baseUrl;
    @Value("${coincap.api-key}")
    private String apiKey;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(this::configureHeaders)
                .requestInterceptor(restClientInterceptor)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    String errorMessage = String.format(
                            "Server error occurred while calling %s. Status code: %s, Response body: %s",
                            request.getURI(),
                            response.getStatusCode(),
                            response.getBody()
                    );
                    log.error(errorMessage);
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

    private void configureHeaders(HttpHeaders headers) {
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
    }

    @Bean
    public RestClientAdapter adapter() {
        return RestClientAdapter.create(restClient());
    }

    @Bean
    public HttpServiceProxyFactory factory() {
        return HttpServiceProxyFactory
                .builderFor(adapter())
                .build();
    }

    @Bean
    public MarketClient marketClient() {
        return factory().createClient(MarketClient.class);
    }

    @Bean
    public ExchangeClient exchangeClient() {
        return factory().createClient(ExchangeClient.class);
    }

    @Bean
    public AssetClient assetClient() {
        return factory().createClient(AssetClient.class);
    }
}