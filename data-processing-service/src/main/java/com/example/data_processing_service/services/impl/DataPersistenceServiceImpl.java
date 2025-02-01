package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.exception.DataNotFoundException;
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
            retryFor = {DataAccessException.class, RuntimeException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    @Override
    public void saveToDatabase(@Nonnull Set<MarketModel> marketModels) throws DataNotFoundException {
        if (marketModels.isEmpty()) {
            throw new DataNotFoundException("Market model set passed but is empty");
        }
        if (marketModels.size() != 100) {
            log.error("Market model set passed but is missing data: {}/100 elements", marketModels.size());
            throw new DataNotFoundException("Market model set passed but does not contain 100 elements");
        }
        marketModelRepository.saveAll(marketModels);
        log.info("Successfully saved all models to database at: {}", LocalDateTime.now());
    }

    @Recover
    public void recoverSaveToDatabase(DataAccessException e, @Nonnull Set<MarketModel> marketModels) {
        log.error("Max number of retries reached! (5/5). Unable to save set size of: {}: {}",
                marketModels.size(), e.getMostSpecificCause().getMessage());
    }
}
