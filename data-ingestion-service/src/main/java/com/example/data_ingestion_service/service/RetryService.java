package com.example.data_ingestion_service.service;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class RetryService {

    private final RetryRegistry registry;

    public RetryService(RetryRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void postConstruct() {
        registry
                .retry("fetchDataRetry")
                .getEventPublisher()
                .onRetry(System.out::println);
    }
}
