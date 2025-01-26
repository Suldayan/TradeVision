package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceServiceImpl implements DataPersistenceService {
    private final MarketModelRepository marketModelRepository;

    @Retryable(
            retryFor = DataAccessException.class,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    @Override
    public void saveToDatabase(@Nonnull Set<MarketModel> marketModels) {
        if (marketModels.isEmpty()) {
            log.warn("Empty marketModels set provided. No data saved");
            return;
        }
        try {
            marketModelRepository.saveAll(marketModels);
            log.info("Successfully saved all models to database at: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to save to database at: {}", LocalDateTime.now());
            throw e;
        }
    }

    @Recover
    private void recoverSaveToDatabase(DataAccessException e, @Nonnull Set<MarketModel> marketModels) {
        log.error("Max number of retries reached! (3/3). Unable to save set size of: {}: {}",
                marketModels.size(), e.getMessage());
    }
}
