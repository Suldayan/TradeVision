package com.example.data_ingestion_service.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class ScheduledApiClientConfig {

    @Value("${client.api.base_url}")
    private String base_url;
    @Value("${rate.limit.max-requests}")
    private int maxRequests;

    @Bean
    public WebClient webClientBuilder() {
        return WebClient.builder()
                .baseUrl(base_url)
                .build();
    }

    @Bean
    public Bandwidth limit() {
        return Bandwidth
                .classic(maxRequests,
                        Refill.greedy(maxRequests, Duration.ofMinutes(1)));
    }

    @Bean
    public Bucket bucket() {
        return (Bucket) Bucket
                .builder()
                .addLimit(limit());
    }
}
