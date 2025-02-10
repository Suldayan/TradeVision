package com.example.trade_strategy_engine.features.shared.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class ApiHttpClient {
    private final RestClientConfig restClientConfig;

    @Bean
    public ProcessingClient processingClient() {
        RestClient client = restClientConfig.restClient();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(ProcessingClient.class);
    }
}