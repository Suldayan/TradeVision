package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.exception.DataValidationException;
import com.example.data_processing_service.services.exception.DatabaseException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceServiceImpl implements DataPersistenceService {
    private final MarketModelRepository marketModelRepository;

    @Override
    public void saveToDatabase(@Nonnull Set<MarketModel> marketModels) throws DatabaseException, DataValidationException {
        validateMarkets(marketModels);
        persistWithRetry(marketModels);
    }

    private void validateMarkets(@Nonnull Set<MarketModel> marketModels) throws DataValidationException {
        if (marketModels.isEmpty()) {
            log.warn("The list of entities for saving has been passed but is empty");
            throw new DataValidationException("Market model set passed but is empty");
        }
        if (marketModels.stream().anyMatch(Objects::isNull)) {
            throw new DataValidationException("Market models set contains null entries");
        }
        if (marketModels.size() != 100) {
            log.error("Market model set passed but is missing data: {}/100 elements", marketModels.size());
            throw new DataValidationException("Market model set passed but does not contain 100 elements");
        }
    }

    @Retryable(
            retryFor = {
                    DataAccessException.class,
                    SQLException.class,
                    TransientDataAccessException.class
            },
            noRetryFor = {
                    IllegalArgumentException.class
            },
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    @Override
    public void persistWithRetry(Set<MarketModel> marketModels) throws DatabaseException {
        try {
            marketModelRepository.saveAll(marketModels);
        } catch (DataAccessException ex) {
            log.error("DataAccessException occurred while saving models. Attempting a retry", ex);
            throw new DatabaseException("Failed to save market data due to database error", ex);
        } catch (RuntimeException ex) {
            log.error("Unexpected runtime error occurred while saving models", ex);
            throw new DatabaseException("Unexpected runtime error while saving market data", ex);
        }
    }

    @Recover
    public void recoverSaveToDatabase(DataAccessException e, @Nonnull Set<MarketModel> marketModels) {
        log.error("Max number of retries reached! (5/5). Unable to save set size of: {}: {}",
                marketModels.size(), e.getMostSpecificCause().getMessage());
    }
}
