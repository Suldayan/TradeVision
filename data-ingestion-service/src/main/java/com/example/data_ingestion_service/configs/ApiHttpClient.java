package com.example.data_ingestion_service.configs;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.clients.ExchangeClient;
import com.example.data_ingestion_service.clients.MarketClient;
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
    public MarketClient marketClient() {
        RestClient client = restClientConfig.restClient();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(MarketClient.class);
    }

    @Bean
    public ExchangeClient exchangeClient() {
        RestClient client = restClientConfig.restClient();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(ExchangeClient.class);
    }

    @Bean
    AssetClient assetClient() {
        RestClient client = restClientConfig.restClient();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(AssetClient.class);
    }
}
