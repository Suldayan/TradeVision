package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.DataAggregateService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataAggregateServiceImpl implements DataAggregateService {
    private final MarketServiceImpl marketService;
    private final ExchangeServiceImpl exchangeService;
    private final AssetServiceImpl assetService;

    final Set<List<?>> dataTypes;
    final double priceThreshold = 0.5;

    /*
    * The list of data types being fetched via api from a http interface
    * */
    private void configureDataTypes() {
        dataTypes.add(marketService.getMarketsAsList());
        dataTypes.add(exchangeService.getExchangeDataAsList());
        dataTypes.add(assetService.getAssetDataAsList());
    }

    /*
    * Takes the lists of data types and streams each fetch in parallel for filtering and partitioning
    * */
    @Retry(name = "apiRetry")
    @TimeLimiter(name = "apiTimeLimiter")
    @CircuitBreaker(name = "apiCircuitBreaker")
    @Scheduled(fixedRateString = "1s")
    @Override
    public void fetchDataAsync() {
        dataTypes.stream()
                .parallel()
                .forEach(this::detectDataType);
    }

    /*
    * Filters the data by only sending meaningful changes to the model attributes
    * @Param takes a generic type T data, representing the different data types from the data type set
    * */
    @Override
    public <T> void sendDataToPartition(@Nonnull @NonNull T data) {

    }

    /*
    * A filter meant to only pass by meaningful changes to the partition service
    * */
    private <T> void detectDataType(@Nonnull @NonNull T data) {
        if (data instanceof RawMarketModel) {
            log.info("Filtering data of market model data type");
            if (hasMeaningfulChangeMarket((RawMarketModel) data)) {

            }
        }
    }

    private boolean hasMeaningfulChangeMarket(RawMarketModel data) {

    }
}

