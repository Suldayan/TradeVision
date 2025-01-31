package com.example.data_processing_service.config;

import com.example.data_processing_service.client.IngestionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@RequiredArgsConstructor
@Configuration
public class ApiHttpClient {
    private final RestClientConfig restClientConfig;

    public IngestionClient ingestionClient() {
        RestClient client = restClientConfig.restClient();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(IngestionClient.class);
    }
}
