package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
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

    private static final Integer EXPECTED_MARKET_SIZE = 100;

    @Override
    public void saveToDatabase(@Nonnull Set<MarketModel> marketModels) throws DatabaseException, IllegalArgumentException {
        validateMarkets(marketModels);
        persistWithRetry(marketModels);
    }

    private void validateMarkets(@Nonnull Set<MarketModel> marketModels) throws IllegalArgumentException {
        if (marketModels.isEmpty()) {
            throw new IllegalArgumentException("Market model set passed but is empty");
        }
        if (marketModels.size() != EXPECTED_MARKET_SIZE) {
            throw new IllegalArgumentException(String.format("Market model set passed but is missing data: %s/100 elements",
                    marketModels.size()));
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

            log.info("Successfully saved all market models");
        } catch (DataAccessException ex) {
            log.error("DataAccessException occurred while saving models. Attempting a retry", ex);
            throw new DatabaseException("Failed to save market data due to database error", ex);
        } catch (RuntimeException ex) {
            throw new DatabaseException("Unexpected runtime error while saving market data", ex);
        }
    }

    @Recover
    public void recoverSaveToDatabase(DataAccessException e, @Nonnull Set<MarketModel> marketModels) {
        log.error("Max number of retries reached! (5/5). Unable to save set size of: {}: {}",
                marketModels.size(), e.getMostSpecificCause().getMessage());
    }
}
