package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.services.*;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/*
* Aggregates data via the coin cap api (served from the services) then filters it by price comparison, only pushing data with a >= 5% change within price or if it doesn't already exist
* Data aggregates on 5 minute intervals with a retry, time limiter, and circuit breaker with Resilience 4j for extra percussion
* The most recent api fetch will be cached via redis, and will be compared with the database (postgres) equivalent for price checks
* The cache will have a lifetime of 5m 10s to ensure it gets properly compared before being erased. (This may be changed for programmatic deletion rather than life cycles)
* Overall aggregation for this service involves a full work flow of ingesting -> filtering -> saving
* @param takes market, exchange, and asset services for its api implementations and market,exchange, and asset model repositories for saving the data
* */

@Service
@Slf4j
@RequiredArgsConstructor
public class DataAggregateServiceImpl implements DataAggregateService {
    private final MarketService marketService;
    private final ExchangeService exchangeService;
    private final AssetService assetService;
    private final DatabaseService databaseService;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Nonnull
    @Override
    public List<RawExchangesModel> fetchExchanges() {
        return exchangeService.getExchangeData();
    }

    @Nonnull
    @Override
    public List<RawAssetModel> fetchAssets() {
        return assetService.getAssetData();
    }

    @Nonnull
    @Override
    public List<RawMarketModel> fetchMarkets() {
        return marketService.getMarketsData();
    }

    @Retry(name = "apiRetry")
    @TimeLimiter(name = "apiTimeLimiter")
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Override
    public CompletableFuture<Void> asyncFetch() {
        CompletableFuture<List<RawExchangesModel>> exchangeFuture = asyncFetch(this::fetchExchanges, "fetchExchanges");
        CompletableFuture<List<RawAssetModel>> assetFuture = asyncFetch(this::fetchAssets, "fetchAssets");
        CompletableFuture<List<RawMarketModel>> marketFuture = asyncFetch(this::fetchMarkets, "fetchMarkets");

        return CompletableFuture.allOf(
                exchangeFuture.thenCompose(this::saveToDatabaseAsync),
                assetFuture.thenCompose(this::saveToDatabaseAsync),
                marketFuture.thenCompose(this::saveToDatabaseAsync)
        ).whenComplete((result, error) -> {
            if (error != null) {
                log.error("Failed to complete all async tasks: {}", error.getMessage(), error);
            } else {
                log.info("All async tasks and database saves have been completed");
            }
        });
    }

    private <T> CompletableFuture<T> asyncFetch(@Nonnull Supplier<T> supplier, @Nonnull String taskName) {
        return CompletableFuture.supplyAsync(supplier, executor)
                .exceptionally(error -> {
                    log.error("An error occurred during the async process for {}: {}", taskName, error.getMessage(), error);
                    throw new DataAggregateException(String.format("Failed to async the %s: %s", taskName, error.getMessage()));
                });
    }

    private <S> CompletableFuture<Void> saveToDatabaseAsync(@Nonnull List<S> entities) {
        return CompletableFuture.runAsync(() -> databaseService.saveToDatabase(entities));
    }
}

