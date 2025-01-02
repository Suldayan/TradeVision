package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.processed.MarketModel;
import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.repository.MarketModelRepository;
import com.example.data_ingestion_service.services.AssetService;
import com.example.data_ingestion_service.services.DataAggregateService;
import com.example.data_ingestion_service.services.ExchangeService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private final MarketModelRepository marketModelRepository;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Nonnull
    @CachePut(value = "exchangeApiResponse")
    @Override
    public List<RawExchangesModel> fetchExchanges() {
        return exchangeService.getExchangeData();
    }

    @Nonnull
    @CachePut(value = "assetApiResponse")
    @Override
    public List<RawAssetModel> fetchAssets() {
        return assetService.getAssetData();
    }

    @Nonnull
    @CachePut(value = "marketApiResponse")
    @Override
    public List<RawMarketModel> fetchMarkets() {
        return marketService.getMarketsData();
    }

    //TODO configure message queuing with kafka/rabbitmq for the event driven flow
    @Retry(name = "apiRetry")
    @TimeLimiter(name = "apiTimeLimiter")
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Scheduled(fixedRateString = "300000") // 5 minute scheduling
    @Override
    public CompletableFuture<Void> asyncFetch() {
        CompletableFuture<List<RawExchangesModel>> exchangeFuture = CompletableFuture.supplyAsync(this::fetchExchanges);
        CompletableFuture<List<RawAssetModel>> assetFuture = CompletableFuture.supplyAsync(this::fetchAssets);
        CompletableFuture<List<RawMarketModel>> marketFuture = CompletableFuture.supplyAsync(this::fetchMarkets);

        return CompletableFuture.allOf(exchangeFuture, assetFuture, marketFuture)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error("Failed to complete all async tasks: {}", error.getMessage(), error);
                            } else {
                                log.info("All async tasks have been completed");
                            }
                        });
    }

    private <T> CompletableFuture<T> asyncFetch(Supplier<T> supplier, String taskName) {
        return CompletableFuture.supplyAsync(supplier, executor)
                .exceptionally(error -> {
                    log.error("An error occurred during the async process for {}: {}", taskName, error.getMessage(), error);
                    throw new DataAggregateException(String.format("Failed to async the %s: %s", taskName, error.getMessage()));
                });
    }

    /*
    * Creates a new list of raw market models containing only markets with meaningful price changes
    * @return a list of market transfer objects that contain these changes
    * */
    @Nonnull
    @Override
    public List<RawMarketModel> collectAndUpdateMarketState() {
        List<RawMarketModel> cachedMarketData = fetchMarkets();
        return cachedMarketData.stream()
                .filter(this::isPriceChangeMeaningful)
                .map(data -> RawMarketModel.builder()
                        .id(data.getId())
                        .rank(data.getRank())
                        .priceQuote(data.getPriceQuote())
                        .priceUsd(data.getPriceUsd())
                        .volumeUsd24Hr(data.getVolumeUsd24Hr())
                        .percentExchangeVolume(data.getPercentExchangeVolume())
                        .tradesCount(data.getTradesCount())
                        .updated(data.getUpdated())
                        .exchangeId(data.getExchangeId())
                        .quoteId(data.getQuoteId())
                        .baseSymbol(data.getBaseSymbol())
                        .quoteSymbol(data.getQuoteSymbol())
                        .build())
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public Set<String> filterExchangeIds() {
        List<RawMarketModel> filteredMarketModels = collectAndUpdateMarketState();
        return filteredMarketModels.stream()
                .map(RawMarketModel::getExchangeId)
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<String> filterAssetIds() {
        List<RawMarketModel> filteredMarketModels = collectAndUpdateMarketState();
        Set<String> baseIds = filteredMarketModels.stream()
                .map(RawMarketModel::getId)
                .collect(Collectors.toSet());
        Set<String> quoteIds = filteredMarketModels.stream()
                .map(RawMarketModel::getQuoteId)
                .collect(Collectors.toSet());
        baseIds.addAll(quoteIds);
        return baseIds;
    }

    @Nonnull
    @Override
    public Set<RawExchangesModel> exchangeIdsToModels() {
        Set<String> exchangeIds = filterExchangeIds();
        List<RawExchangesModel> unfilteredModels = fetchExchanges();
        return unfilteredModels.stream()
                .filter(model -> exchangeIds.contains(model.getId()))
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<RawAssetModel> assetIdsToModels() {
        Set<String> assetIds = filterAssetIds();
        List<RawAssetModel> unfilteredAssets = fetchAssets();
        return unfilteredAssets.stream()
                .filter(model -> assetIds.contains(model.getId()))
                .collect(Collectors.toSet());
    }

    /*
    * Determines whether the price change is meaningful enough to be saved into the database unless it does not already exist within it
    * @param takes a type MarketDTO to be used for price comparison
    * @return a boolean if the price change is more than or equal to 5%
    * */
    @Override
    public Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData) {
        Optional<MarketModel> lastSignificantChange = marketModelRepository.findById(cachedData.getId());
        if (lastSignificantChange.isEmpty()) {
            return true;
        }
        // Get the last significant price
        BigDecimal lastPrice = lastSignificantChange.get().getPriceUsd();
        if (lastPrice.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        // Calculate percentage change
        BigDecimal currentPrice = cachedData.getPriceUsd();
        BigDecimal percentageChange = currentPrice
                .subtract(lastPrice)
                .abs()
                .divide(lastPrice, MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100));
        // Check if the change is >= 5%
        return percentageChange.compareTo(BigDecimal.valueOf(5)) >= 0;
    }
}

