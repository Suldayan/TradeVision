package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.processed.AssetModel;
import com.example.data_ingestion_service.models.processed.ExchangeModel;
import com.example.data_ingestion_service.models.processed.MarketModel;
import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.repository.RawAssetModelRepository;
import com.example.data_ingestion_service.repository.RawExchangeModelRepository;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DataAggregateService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    // TODO: configure the processed repositories and replace these with those
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
        return exchangeService.getExchangeData();
    }

    /*
    * Fetches and caches the asset api response
    * @return a list of raw asset models to cache
    * */
    @Nonnull
    @CachePut(value = "assetApiResponse")
    @Override
    public List<RawAssetModel> fetchAssets() {
        return assetService.getAssetData();
    }

    /*
     * Fetches and caches the market api response
     * @return a list of raw market models to cache
     * */
    @Nonnull
    @CachePut(value = "marketApiResponse")
    @Override
    public List<RawMarketModel> fetchMarkets() {
        return marketService.getMarketsData();
    }

    /*
    * Runs the assets and exchanges fetch functions asynchronously with the markets for matched responses by fetch time
    * */
    //TODO more than likely, we'll have to configure scheduling and activation for the overall file within this function. This function should be the central orchestration
    @Retry(name = "apiRetry")
    @TimeLimiter(name = "apiTimeLimiter")
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Scheduled(fixedRateString = "300000") // 5 minute scheduling
    @Override
    public void asyncFetch() {
            CompletableFuture
                    .supplyAsync(this::fetchAssets)
                    .thenRunAsync(this::fetchMarkets)
                    .thenRunAsync(this::fetchExchanges)
                    .exceptionally(error -> {
                        // TODO add better error handling later
                        throw new ApiException(error.getMessage());
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
        return cachedMarketData.parallelStream()
                .map(data -> {
                    if (!isPriceChangeMeaningful(data)) {
                        // Then we can just ignore
                        log.debug("No significant change to price data, this message is for debugging purposes");
                    }
                    return RawMarketModel.builder()
                            .id(data.getId())
                            .rank(data.getRank())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /*
    * Determines whether the price change is meaningful enough to be saved into the database unless it does not already exist within it
    * @param takes a type MarketDTO to be used for price comparison
    * @return a boolean if the price change is more than or equal to 5%
    * */
    @Override
    public Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData) {
        Optional<RawMarketModel> lastSignificantChange = marketModelRepository.findById(cachedData.getId());
        if (lastSignificantChange.isEmpty()) {
            // TODO: Instead of doing this type of conversion, we can use our new processed models, create a new entity using the builder and we can save it to the database
            // TODO: But we must figure out how to use the dto properly as it contains each id connected to each other
            // TODO: Possibly replace dtoToEntity with the completeModelAttributes function instead??

            /*
            * TODO: A new possible flow could be that we receive the data via caching fetch functions -> save the id and price relationships within the market dto -> compare the prices
            *  for validity -> if empty, we create the model and save it to the db/otherwise, we boolean check -> create the new models by parallel streaming through the market dto
            *  attributes so each model has the right relationships with each other -> save to db
            * */

            // TODO: convert the cached response into a processed response with all of its relationships
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

    /*
     * Fills the market models with its corresponding base/quote assets and exchanges, then saves to the database
     * */
    @CacheEvict(cacheNames = "marketApiResponse, exchangeApiResponse, assetApiResponse")
    @Override
    public void completeModelAttributes() throws DataAggregateException {
        List<RawMarketModel> meaningfulMarketState = collectAndUpdateMarketState();
        List<RawExchangesModel> cachedExchanges = fetchExchanges();
        List<RawAssetModel> cachedAssets = fetchAssets();

        //TODO: we can completable future this and async the save onto the db function
        List<ExchangeModel> exchangeModels = cachedExchanges.parallelStream()
                .map(attribute -> ExchangeModel.builder()
                        .id(attribute.getId())
                        .name(attribute.getName())
                        .percentTotalVolume(attribute.getPercentTotalVolume())
                        .volumeUsd(attribute.getVolumeUsd())
                        .updated(attribute.getUpdated())
                        .markets(null) // Set as null initially until all models are built, then we go over the relationships and set them
                        .build())
                .toList();

        List<AssetModel> assetModels = cachedAssets.parallelStream()
                .map(attribute -> AssetModel.builder()
                        .id(attribute.getId())
                        .symbol(attribute.getSymbol())
                        .name(attribute.getName())
                        .supply(attribute.getSupply())
                        .maxSupply(attribute.getMaxSupply())
                        .marketCapUsd(attribute.getMarketCapUsd())
                        .volumeUsd24Hr(attribute.getVolumeUsd24Hr())
                        .priceUsd(attribute.getPriceUsd())
                        .changePercent24Hr(attribute.getChangePercent24Hr())
                        .vwap24Hr(attribute.getVwap24Hr())
                        .baseMarkets(null) // We set these as null as well as they'll be synchronized during the market streaming and defined at that time
                        .quoteMarkets(null)
                        .build())
                .toList();

        List<MarketModel> marketModels = meaningfulMarketState.parallelStream()
                .map(attribute -> MarketModel.builder()
                        .id(attribute.getId())
                        .exchange(
                                exchangeModels.parallelStream()
                                        .filter(exchangeModel -> Objects.equals(attribute.getExchangeId(), exchangeModel.getId()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding exchange model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .baseAsset(
                                assetModels.parallelStream()
                                        .filter(baseModel -> Objects.equals(attribute.getBaseSymbol(), baseModel.getId()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding base asset model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .quoteAsset(
                                assetModels.parallelStream()
                                        .filter(quoteModel -> Objects.equals(attribute.getBaseSymbol(), quoteModel.getId()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding quote asset model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .baseSymbol(attribute.getBaseSymbol())
                        .quoteSymbol(attribute.getQuoteSymbol())
                        .priceQuote(attribute.getPriceQuote())
                        .priceUsd(attribute.getPriceUsd())
                        .volumeUsd24Hr(attribute.getVolumeUsd24Hr())
                        .percentExchangeVolume(attribute.getPercentExchangeVolume())
                        .tradesCount(attribute.getTradesCount())
                        .updated(attribute.getUpdated())
                        .build())
                .toList();
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

