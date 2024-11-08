package com.example.data_ingestion_service.config;

import io.github.bucket4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {

    @Value("${limits.max-requests}")
    private int maxRequests;

    @Value("${limits.refresh-rate}")
    private int refreshRate;

    @Bean
    public Bucket bucket() {
        Bandwidth limit = Bandwidth.classic(maxRequests, Refill.greedy(maxRequests, Duration.ofMinutes(refreshRate)));
        return Bucket.builder().addLimit(limit).build();
    }
}
