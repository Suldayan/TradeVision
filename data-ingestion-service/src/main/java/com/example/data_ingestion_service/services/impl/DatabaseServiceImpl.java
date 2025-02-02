package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
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
public class DatabaseServiceImpl implements DatabaseService {
    private final RawMarketModelRepository marketModelRepository;

    @Override
    public void saveToDatabase(@Nonnull Set<RawMarketModel> marketModels) throws DatabaseException {
        validateMarketModels(marketModels);
        persistWithRetry(marketModels);
    }

    private void validateMarketModels(@Nonnull Set<RawMarketModel> marketModels) {
        if (marketModels.isEmpty()) {
            log.warn("The list of entities for saving has been passed but is empty");
            throw new IllegalArgumentException("Market models passed null check but is empty");
        }
        if (marketModels.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Market models set contains null entries");
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
    public void persistWithRetry(@Nonnull Set<RawMarketModel> marketModels) throws DatabaseException {
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
    public void recoverSaveToDatabase(DatabaseException ex, Set<RawMarketModel> lostData) throws DatabaseException {
        log.error("All retries have been exhausted. Data lost for: {}. Error: {}", lostData, ex.getMessage());

        // For now, we throw an exception but there's definitely a better way to handle the data. Perhaps DLQ or something??
        throw new DatabaseException("Failed to save market models through all retires", ex);
    }
}
