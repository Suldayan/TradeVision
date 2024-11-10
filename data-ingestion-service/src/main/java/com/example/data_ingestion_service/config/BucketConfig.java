package com.example.data_ingestion_service.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {

    @Value("${bucket.rate-limit:500}")
    private long rateLimit;

    @Value("${bucket.time-period:60}")
    private long timePeriodInSeconds;

    @Bean
    public Bandwidth bandwidth() {
        return Bandwidth.builder()
                .capacity(rateLimit)
                .refillGreedy(rateLimit, Duration.ofSeconds(timePeriodInSeconds))
                .build();
    }

    @Bean
    public Bucket bucket(Bandwidth bandwidth) {
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}