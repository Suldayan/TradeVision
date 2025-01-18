package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.*;
import com.example.data_ingestion_service.services.exceptions.AsyncException;
import com.example.data_ingestion_service.services.exceptions.ValidationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/*
* Aggregates data via the coin cap api (served from the services) then filters it by price comparison, only pushing data with a >= 5% change within price or if it doesn't already exist
* Data aggregates on 5 minute intervals with a circuit breaker via Resilience 4j if there are multiple api failures
* The most recent api fetch will be cached via redis, and will be compared with the database (postgres) equivalent for price checks
* The cache will have a lifetime of 5m 10s to ensure it gets properly compared before being erased. (This may be changed for programmatic deletion rather than life cycles)
* Overall aggregation for this service involves a full work flow of ingesting -> filtering -> saving
* @param takes market, exchange, and asset services for its api implementations and market,exchange, and asset model repositories for saving the data
* */

@Service
@Slf4j
@RequiredArgsConstructor
public class DataAsyncServiceImpl implements DataAsyncService {
    private final MarketService marketService;
    private final ExchangeService exchangeService;
    private final AssetService assetService;
    private final DatabaseService databaseService;

    @Async("apiExecutor")
    @Nonnull
    @Override
    public CompletableFuture<Set<RawExchangesModel>> fetchExchanges() {
        return CompletableFuture.supplyAsync(() -> {
                    Set<RawExchangesModel> models = exchangeService.convertToModel();
                    if (models == null) {
                        throw new ValidationException("(Exchanges) convertToModel returned null result");
                    }
                    if (models.isEmpty()) {
                        log.warn("Exchange models retrieved but is empty");
                        throw new ValidationException("(Exchanges) convertToModel passed null check but returned an empty set");
                    }
                    return models;
                })
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Fetched asynchronous exchanges with result size: {}",
                                result != null ? result.size() : 0);
                    }
                })
                .exceptionally(ex -> {
                    log.error("Failed to fetch exchanges asynchronously: {}", ex.getMessage(), ex);
                    if (ex.getCause() instanceof ValidationException) {
                        throw (ValidationException) ex.getCause();
                    }
                    throw new AsyncException("Exchange fetch has failed on the asynchronous flow", ex);
                });
    }

    @Async("apiExecutor")
    @Nonnull
    @Override
    public CompletableFuture<Set<RawAssetModel>> fetchAssets() {
        return CompletableFuture.supplyAsync(() -> {
            Set<RawAssetModel> models = assetService.convertToModel();
            if (models == null) {
                throw new ValidationException("(Assets) convertToModel returned null result");
            }
            if (models.isEmpty()) {
                log.warn("Asset models retrieved but is empty");
                throw new ValidationException("(Assets) convertToModel passed null check but returned an empty set");
            }
            return models;
            })
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Fetched asynchronous assets with result size: {}",
                            result != null ? result.size() : 0);
                }
            })
            .exceptionally(ex -> {
                log.error("Failed to fetch assets asynchronously: {}", ex.getMessage(), ex);
                if (ex.getCause() instanceof ValidationException) {
                    throw (ValidationException) ex.getCause();
                }
                throw new AsyncException("Asset fetch has failed on the asynchronous flow", ex);
            });
    }

    @Async("apiExecutor")
    @Nonnull
    @Override
    public CompletableFuture<Set<RawMarketModel>> fetchMarkets() {
        return CompletableFuture.supplyAsync(() -> {
            Set<RawMarketModel> models = marketService.convertToModel();
            if (models == null) {
                throw new ValidationException("(Markets) convertToModel returned null result");
            }
            if (models.isEmpty()) {
                log.warn("Market models retrieved but is empty");
                throw new ValidationException("(Markets) convertToModel passed null check but returned an empty set");
            }
            return models;
            })
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Fetched asynchronous markets with result size: {}",
                            result != null ? result.size() : 0);
                }
            })
            .exceptionally(ex -> {
                log.error("Failed to fetch market asynchronously: {}", ex.getMessage(), ex);
                if (ex.getCause() instanceof ValidationException) {
                    throw (ValidationException) ex.getCause();
                }
                throw new AsyncException("Market fetch has failed on the asynchronous flow", ex);
            });
    }

    // TODO properly configure and test this
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Override
    public CompletableFuture<Void> asyncFetch() {
        return CompletableFuture.allOf(
                fetchExchanges().thenCompose(this::saveToDatabaseAsync),
                fetchAssets().thenCompose(this::saveToDatabaseAsync),
                fetchMarkets().thenCompose(this::saveToDatabaseAsync)
        ).whenComplete((result, error) -> {
            if (error != null) {
                log.error("Failed to complete all async tasks: {}", error.getMessage(), error);
            } else {
                log.info("All async tasks completed successfully");
            }
        }).exceptionally(ex -> {
            log.error("Failed asynchronous fetching: {}", ex.getMessage(), ex);
            throw new AsyncException("Async fetch has failed on an exceptionally error", ex);
        });
    }

    private <S> CompletableFuture<Void> saveToDatabaseAsync(@Nonnull Set<S> entities) {
        return CompletableFuture.runAsync(() -> databaseService.saveToDatabase(entities))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send entities to database service: {}", ex.getMessage(), ex);
                    }
                    log.info("Successfully sent entity of type: {} to database service at: {}", entities.getClass(), LocalTime.now());
                })
                .exceptionally(error -> {
                    log.error("Failed to save {} to database at: {}, {}", entities.getClass(), LocalTime.now(), error.getMessage(), error);
                    return null;
                });
    }
}

