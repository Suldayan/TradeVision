package com.example.data_ingestion_service.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {

    @Value("${limits.max-requests}")
    private int MAX_REQUESTS;

    @Value("${limits.refresh-rate}")
    private int REFRESH_RATE;

    @Bean
    public Bandwidth limit() {
        return Bandwidth
                .classic(MAX_REQUESTS,
                        Refill.greedy(MAX_REQUESTS, Duration.ofMinutes(REFRESH_RATE)));
    }

    @Bean
    public Bucket bucket() {
        return (Bucket) Bucket
                .builder()
                .addLimit(limit());
    }
}
