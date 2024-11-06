package com.example.data_ingestion_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
@Slf4j
public class DataFilterService<T> {

    private final KafkaProducerService<T> producerService;
    private final AtomicReference<Object> previousModelState;

    public DataFilterService(KafkaProducerService<T> producerService,
                             @Value("${limits.max-retries}") int maxRetries) {
        this.producerService = producerService;
        this.previousModelState = new AtomicReference<>();
    }

    public void filter(T response) {
        try {
            if (!response.equals(previousModelState.get())) {
                previousModelState.set(response);
                producerService.sendData(response);
            }

            log.info("Data is unchanged as of: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Unable to compare response with its previous state: {}", e.getMessage());
        }
    }
}