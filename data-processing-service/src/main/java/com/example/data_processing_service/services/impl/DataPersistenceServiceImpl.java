package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.processed.MarketModel;
import com.example.data_processing_service.repository.processed.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceServiceImpl implements DataPersistenceService {
    private final MarketModelRepository marketModelRepository;

    @Retryable(
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    @Override
    public void saveToDatabase(@Nonnull Set<@Valid MarketModel> marketModels) {
        if (marketModels.isEmpty()) {
            log.warn("Market set has been passed but is empty");
        }
        try {
            marketModelRepository.saveAll(marketModels);
            log.info("Successfully saved all models to database at: {}", LocalTime.now());
        } catch (Exception ex) {
            log.error("Failed to save to database at: {}", LocalTime.now());
        }
    }
}
