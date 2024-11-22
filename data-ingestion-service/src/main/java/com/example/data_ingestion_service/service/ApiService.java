package com.example.data_ingestion_service.service;

import com.example.data_ingestion_service.client.ApiClient;
import com.example.data_ingestion_service.model.RawAssetWrapperModel;
import com.example.data_ingestion_service.model.RawExchangeWrapperModel;
import com.example.data_ingestion_service.model.RawMarketWrapperModel;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class ApiService {

    private final ApiClient apiClient;
    private final FilterService filterService;
    private final Timer timer;

    public ApiService(ApiClient apiClient, FilterService filterService, Timer timer) {
        this.apiClient = apiClient;
        this.filterService = filterService;
        this.timer = timer;
    }
    
    /*
    * Helper functions for supporting the supply async lambda
    * @return the response entity as its corresponding model object from the api client within a lambda
    * representing a timer for api responses
    * */
    public RawMarketWrapperModel getMarketApiResponseAsBody() {
        return trackApiResponseTime(() -> apiClient.getAllMarketData().getBody());
    }
    public RawExchangeWrapperModel getExchangeApiResponseAsBody() {
        return trackApiResponseTime(() -> apiClient.getAllExchangeData().getBody());
    }
    public RawAssetWrapperModel getAssetApiResponseAsBody() {
        return trackApiResponseTime(() -> apiClient.getAllAssetData().getBody());
    }

    /*
    * Helper function for tracking api response times for data
    * @param takes Supplier<T> data, representing the data being returned from the helper functions
    * @return generic type T, representing the response time in which is logged
    * */
    public <T> T trackApiResponseTime(Supplier<T> data) {
        return timer.record(data);
    }

    // Helper functions for lambda support and sending data to the filter service
    public void sendMarketDataToFilter(RawMarketWrapperModel data) {
        log.info("Sending market data to filter at: {}", LocalDateTime.now());
        filterService.processMarketData(data);
    }
    public void sendExchangeDataToFilter(RawExchangeWrapperModel data) {
        log.info("Sending exchange data to filter at: {}", LocalDateTime.now());
        filterService.processExchangeData(data);
    }
    public void sendAssetDataToFilter(RawAssetWrapperModel data) {
        log.info("Sending asset data to filter at: {}", LocalDateTime.now());
        filterService.processAssetHistoryData(data);
    }

    //TODO: figure out how to reconfigure throwing the custom exception
    // Helper function for centralized error handling that throws a custom exception, CustomApiServiceException
    public Void handleError(Throwable error) {
        log.error("An error has occurred during the asynchronous chain of fetching: {}",
                error.getMessage());
        return null;
    }

    // Helper function for centralized logging within the asynchronous chain
    public void logAsynchronousChain() {
        log.info("Data successfully fetched and sent to filter");
        log.debug("Data successfully fetched and sent to filter at: {}", LocalDateTime.now());

        //TODO: configure a bucket counter for log debugging
    }

    //TODO: wrap with bucket -> retry -> circuit breaking
    /*
    * Asynchronous functions for fetching its respective data, then running it through the filter service
    * @return a CompletableFuture<Void> to represent the functions fully finished state through the
    * asynchronous chain
    * */
    @Scheduled(fixedRateString = "${api.scheduled.interval:6000}")
    @Async
    public CompletableFuture<Void> fetchMarketData() {
        log.info("Fetching market data");
        log.debug("Fetching market data at: {}", LocalDateTime.now());

        return CompletableFuture.supplyAsync(this::getMarketApiResponseAsBody)
                .thenAccept(this::sendMarketDataToFilter)
                .thenRun(this::logAsynchronousChain)
                .exceptionally(this::handleError);
    }

    @Scheduled(fixedRateString = "${api.scheduled.interval:6000}")
    @Async
    public CompletableFuture<Void> fetchExchangeData() {
        log.info("Fetching exchange data");
        log.debug("Fetching exchange data at: {}", LocalDateTime.now());

        return CompletableFuture.supplyAsync(this::getExchangeApiResponseAsBody)
                .thenAccept(this::sendExchangeDataToFilter)
                .thenRun(this::logAsynchronousChain)
                .exceptionally(this::handleError);
    }

    @Scheduled(fixedRateString = "${api.scheduled.interval:6000}")
    @Async
    public CompletableFuture<Void> fetchAssetData() {
        log.info("Fetching asset data");
        log.debug("Fetching asset data at: {}", LocalDateTime.now());

        return CompletableFuture.supplyAsync(this::getAssetApiResponseAsBody)
                .thenAccept(this::sendAssetDataToFilter)
                .thenRun(this::logAsynchronousChain)
                .exceptionally(this::handleError);
    }
}
