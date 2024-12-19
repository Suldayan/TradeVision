package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.DataAggregateService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataAggregateServiceImpl implements DataAggregateService {
    private final MarketServiceImpl marketService;
    private final ExchangeServiceImpl exchangeService;
    private final AssetServiceImpl assetService;

    //TODO possibly change set into List and ? into Object
    public final List<List<?>> dataTypes = new ArrayList<>();

    /*
     * The list of data types being fetched via api from a http interface
     * */
    @PostConstruct
    private void configureDataTypes() {
        //TODO: dataTypes is throwing null pointer due to the services throwing null
        dataTypes.add(marketService.getMarketsAsList());
        dataTypes.add(exchangeService.getExchangeDataAsList());
        dataTypes.add(assetService.getAssetDataAsList());
        log.info("Data types configured: {}", dataTypes.size());
    }

    /*
     * Takes the lists of data types and streams each fetch in parallel for filtering and partitioning
     * */
    @Retry(name = "apiRetry")
    @TimeLimiter(name = "apiTimeLimiter")
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Scheduled(fixedRateString = "1000")
    @Override
    public void fetchDataAsync() throws DataAggregateException {
        //TODO make sure to release memory of the list after each scheduled fetch
        dataTypes.stream()
                .parallel()
                .forEach(dataType -> {
                    try {
                        log.debug("Streaming data types at: {}", LocalDateTime.now());
                        dataType.forEach(this::detectDataType);
                    } catch (Exception e) {
                        log.error("Error processing data at: {}", LocalDateTime.now());
                        throw new DataAggregateException(String.format("Error processing data type: %s", e));
                    }
                });
    }

    /*
     * Finds the class of the data and brings it to a filter
     * @Param takes a generic type t data, representing the api fetched data
     * */
    @Override
    public <T> void detectDataType(@Nonnull T data) {
        switch (data) {
            case RawMarketModel rawMarketModel -> {
                log.info("Detected data of type market");
                // cacheMarketData((RawMarketModel) data);
            }
            case RawExchangesModel rawExchangesModel -> {
                log.info("Detected data of type exchange");
                // cacheExchangeData((RawMarketModel) data);
            }
            case RawAssetModel rawAssetModel -> {
                log.info("Detected data of type asset");
                // cacheAssetData((RawMarketModel) data);
            }
            default -> log.warn("Data has been passed but not detected as any valid entity");
        }
    }

    //TODO: test fetch functions before configuring redis
    /*
    * Helper function for caching data
    * @Param takes an entity of RawMarketModel, fetched from the detectDataType function
    * @return a RawMarketModel, supporting the cache annotation
    *
    @CachePut("marketData")
    private RawMarketModel cacheMarketData(@Nonnull RawMarketModel data) {
        return data;
    }

    /*
    * Compares the market attributes with its previous successful save only pushing significant changes
    * @Param takes a RawMarketModel entity data
    * @return a boolean, determining whether the changes were significant or not
    *
    private boolean hasMeaningfulChangeMarket(@Nonnull RawMarketModel data) {
        RawMarketModel lastUpdatedData = cachedData;


    }
    */
}

