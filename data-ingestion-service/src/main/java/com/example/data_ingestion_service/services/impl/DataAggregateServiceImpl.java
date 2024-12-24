package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.*;
import com.example.data_ingestion_service.repository.RawAssetModelRepository;
import com.example.data_ingestion_service.repository.RawExchangeModelRepository;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DataAggregateService;
import com.example.data_ingestion_service.services.dto.MarketDTO;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
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
    private final MarketServiceImpl marketService;
    private final ExchangeServiceImpl exchangeService;
    private final AssetServiceImpl assetService;
    private final RawMarketModelRepository marketModelRepository;
    private final RawExchangeModelRepository exchangeModelRepository;
    private final RawAssetModelRepository assetModelRepository;

    /*
    * Fetches and caches the exchange api response
    * @return a list of raw exchange models to cache
    * */
    @Nonnull
    @CachePut(value = "exchangeApiResponse")
    @Override
    public List<RawExchangesModel> fetchExchanges() {
        return exchangeService.getExchangeDataAsList();
    }

    /*
    * Fetches and caches the asset api response
    * @return a list of raw asset models to cache
    * */
    @Nonnull
    @CachePut(value = "assetApiResponse")
    @Override
    public List<RawAssetModel> fetchAssets() {
        return assetService.getAssetDataAsList();
    }

    /*
    * Runs the assets and exchanges fetch functions asynchronously with the markets for matched responses by fetch time
    * */
    @Override
    public void asyncFetch() {
            CompletableFuture.supplyAsync(this::fetchAssets)
                    .thenRunAsync(this::fetchExchanges)
                    .exceptionally(error -> {
                        throw new ApiException(error.getMessage());
                    });
    }

    /*
    * Periodically fetch market data every 5 minutes, storing the data in a cache for price comparison
    * @return a list of market DTOs
    * */
    @Retry(name = "apiRetry")
    @TimeLimiter(name = "apiTimeLimiter")
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Scheduled(fixedRateString = "300000") // 5 minute scheduling
    @CachePut(value = "marketApiResponse")
    @Nonnull
    @Override
    public List<MarketDTO> fetchScheduledMarketPrice() throws DataAggregateException {
        List<RawMarketModel> marketData = marketService.getMarketsData();
        return marketData.parallelStream()
                .map(attribute -> MarketDTO.builder()
                        .baseId(attribute.getId())
                        .quoteId(attribute.getQuoteId())
                        .exchangeId(attribute.getExchangeId())
                        .currentPrice(attribute.getPriceUsd())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /*
    * Creates a new list of markets containing only markets with meaningful price changes
    * @return a list of market transfer objects that contain these changes
    * */
    @Nonnull
    @Override
    public List<MarketDTO> collectAndUpdateMarketState() {
        List<MarketDTO> cachedMarketData = fetchScheduledMarketPrice();
        return cachedMarketData.parallelStream()
                .map(data -> {
                    if (!isPriceChangeMeaningful(data)) {
                        // Then we can just ignore
                        log.debug("No significant change to price data, this message is for debugging purposes");
                    }
                    return MarketDTO.builder()
                            .baseId(data.getBaseId())
                            .quoteId(data.getQuoteId())
                            .exchangeId(data.getExchangeId())
                            .currentPrice(data.getCurrentPrice())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /*
    * Determines whether the price change is meaningful enough to be saved into the database unless it does not already exist within it
    * @return a boolean, true, if the price change is more than or equal to 5%
    * */
    @Override
    public Boolean isPriceChangeMeaningful(@Nonnull MarketDTO cachedData) {
        Optional<RawMarketModel> lastSignificantChange = marketModelRepository.findById(cachedData.getBaseId());
        if (lastSignificantChange.isEmpty()) {
            // Convert DTO to entity and save
            RawMarketModel newEntry = dtoToEntity(cachedData);
            marketModelRepository.save(newEntry);
            return true;
        }
        // Get the last significant price
        BigDecimal lastPrice = lastSignificantChange.get().getPriceUsd();
        if (lastPrice.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        // Calculate percentage change
        BigDecimal currentPrice = cachedData.getCurrentPrice();
        BigDecimal percentageChange = currentPrice
                .subtract(lastPrice)
                .abs()
                .divide(lastPrice, MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100));
        // Check if the change is >= 5%
        return percentageChange.compareTo(BigDecimal.valueOf(5)) >= 0;
    }

    /*
     * Fills the market models with its corresponding base/quote assets and exchanges, then saves to the database
     * */
    @Override
    public void completeMarketAttributes() throws DataAggregateException {
        List<MarketDTO> meaningfulMarketState = collectAndUpdateMarketState();
        List<RawExchangesModel> cachedExchanges = fetchExchanges();
        List<RawAssetModel> cachedAssets = fetchAssets();
        meaningfulMarketState.parallelStream()
                        .forEach(attribute -> {
                            //TODO match each attribute with the corresponding fetched data
                        });
        //TODO make sure to release memory of the list after each scheduled fetch
    }

    /*
    * Helper function for dto -> entity conversion specifically for the Markets
    * @param takes a market dto for conversion
    * @return a RawMarketModel after successful conversion
    * */
    // TODO however, we can most likely create our own custom market model for only containing the variables we want
    @Nonnull
    private RawMarketModel dtoToEntity(@Nonnull MarketDTO dto) {
        return new RawMarketModel(
                dto.getBaseId(),
                dto.getCurrentPrice()
        );
    }

    /*
    * Helper generic function for saving to the database
    * @param takes a generic type T, entity, and will detect the class type of that entity and save it to its corresponding repository
    * */
    //TODO if we create a custom market model, we will have to change the instance of this model
    @Transactional
    private <T> void saveToDatabase(@Nonnull T entity) {
        switch (entity) {
            case entity instanceof RawMarketModel:
                marketModelRepository.save((RawMarketModel) entity);
                log.debug("Saving data of type: Market");
                break;
            case entity instanceof RawExchangesModel:
                exchangeModelRepository.save((RawExchangesModel) entity);
                log.debug("Saving data of type: Exchange");
                break;
            case entity instanceof RawAssetModel:
                assetModelRepository.save((RawAssetModel) entity);
                log.debug("Saving data of type: Asset");
                break;
            default -> log.warn("Info was sent to be saved but was not a recognizable type");
        }
    }
}

