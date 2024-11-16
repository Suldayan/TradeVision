package com.example.data_ingestion_service.service;

import com.example.data_ingestion_service.client.ApiClient;
import com.example.data_ingestion_service.exception.CustomApiServiceException;
import com.example.data_ingestion_service.model.RawAssetWrapperModel;
import com.example.data_ingestion_service.model.RawExchangeWrapperModel;
import com.example.data_ingestion_service.model.RawMarketWrapperModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ApiService {

    private final ApiClient apiClient;
    private final FilterService filterService;

    public ApiService(ApiClient apiClient, FilterService filterService) {
        this.apiClient = apiClient;
        this.filterService = filterService;
    }

    /*
     * Periodically fetches all data from the coin cap api
     * */
    @Scheduled(fixedRateString = "${api.scheduled.interval:6000}")
    public void scheduleDataFetch() {
        log.info("Starting scheduled data fetch at: {}", LocalDateTime.now());
        sendDataToFilter();
    }

    //TODO: wrap with retry -> circuit breaking -> scheduled
    public ResponseEntity<RawMarketWrapperModel> fetchMarketData() throws CustomApiServiceException{
        try {
            log.info("Fetching market data");
            log.debug("Fetching market data at: {}", LocalDateTime.now());
            return apiClient.getAllMarketData();
        } catch (Exception error) {
            log.error("Failed to fetch market data: {}", error.getMessage());
            String errorMessage = String.format(
                    "Error while fetching market data at: %s, %s",
                    LocalDateTime.now(), error.getMessage());
            throw new CustomApiServiceException(errorMessage);
        }
    }

    public ResponseEntity<RawExchangeWrapperModel> fetchExchangeData() throws CustomApiServiceException {
        try {
            log.info("Fetching exchange data");
            log.debug("Fetching exchange data at: {}", LocalDateTime.now());
            return apiClient.getAllExchangeData();
        } catch (Exception error) {
            log.error("Failed to fetch exchange data: {}", error.getMessage());
            String errorMessage = String.format(
                    "Error while fetching exchange data at: %s, %s",
                    LocalDateTime.now(), error.getMessage());
            throw new CustomApiServiceException(errorMessage);
        }
    }

    public ResponseEntity<RawAssetWrapperModel> fetchAssetData() throws CustomApiServiceException {
        try {
            log.info("Fetching asset data");
            log.debug("Fetching asset data at: {}", LocalDateTime.now());
            return apiClient.getAllAssetData();
        } catch (Exception error) {
            log.error("Failed to fetch asset data: {}", error.getMessage());
            String errorMessage = String.format(
                    "Error while fetching asset data at: %s, %s",
                    LocalDateTime.now(), error.getMessage());
            throw new CustomApiServiceException(errorMessage);
        }
    }

    public void sendDataToFilter() {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<RawMarketWrapperModel> marketData = fetchMarketData();
                filterService.processMarketData(marketData.getBody());
                return null;
            } catch (CustomApiServiceException e) {
                log.error("Failed to process market data");
                return null;
            }
        }));

        futures.add(CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<RawExchangeWrapperModel> exchangeData = fetchExchangeData();
                filterService.processExchangeData(exchangeData.getBody());
                return null;
            } catch (CustomApiServiceException e) {
                log.error("Failed to process exchange data");
                return null;
            }
        }));

        futures.add(CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<RawAssetWrapperModel> assetData = fetchAssetData();
                filterService.processAssetHistoryData(assetData.getBody());
                return null;
            } catch (CustomApiServiceException e) {
                log.error("Failed to process asset history data");
                return null;
            }
        }));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(throwable -> {
                    log.error("Error processing data", throwable);
                    return null;
                });
    }
}
