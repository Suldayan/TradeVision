package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.DataAsyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataAsyncServiceTest {

    @Autowired
    DataAsyncService dataAsyncService;

    @Test
    void fetchExchanges_ReturnsCompletedFuture_OfExchangeModels() {
        CompletableFuture<Set<RawExchangesModel>> apiResponse = dataAsyncService.fetchExchanges();

        assertNotNull(apiResponse);

        Set<RawExchangesModel> result = assertDoesNotThrow(() ->
                apiResponse.get(5, TimeUnit.SECONDS));

        assertNotNull(result);
    }

    @Test
    void fetchAssets_ReturnsCompletedFuture_OfAssetModels() {
        CompletableFuture<Set<RawAssetModel>> apiResponse = dataAsyncService.fetchAssets();

        assertNotNull(apiResponse);

        // TODO find the source of the explorer variable in asset record and model for why it's null when it shouldn't be
        Set<RawAssetModel> result = assertDoesNotThrow(() ->
                apiResponse.get(5, TimeUnit.SECONDS));

        assertNotNull(result);
    }

    @Test
    void fetchMarkets_ReturnsCompletedFuture_OfMarketModels() {
        CompletableFuture<Set<RawMarketModel>> apiResponse = dataAsyncService.fetchMarkets();

        assertNotNull(apiResponse);

        Set<RawMarketModel> result = assertDoesNotThrow(() ->
                apiResponse.get(5, TimeUnit.SECONDS));
    }

    @Test
    void asyncFetch_Completes_AllAsyncTasks() {
        CompletableFuture<Void> completedAsyncTasks = dataAsyncService.asyncFetch();

        assertDoesNotThrow(() -> dataAsyncService.asyncFetch());
        assertTrue(completedAsyncTasks.isDone());
    }
}
