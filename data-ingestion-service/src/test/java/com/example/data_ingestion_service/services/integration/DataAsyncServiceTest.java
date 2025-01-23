package com.example.data_ingestion_service.services.integration;

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
        Set<RawExchangesModel> result = assertDoesNotThrow(() -> apiResponse.get(5, TimeUnit.SECONDS));

        assertNotNull(apiResponse);
        assertNotNull(result);
        assertEquals(100, result.size(), "Set size should be 100");
    }

    @Test
    void fetchAssets_ReturnsCompletedFuture_OfAssetModels() {
        CompletableFuture<Set<RawAssetModel>> apiResponse = dataAsyncService.fetchAssets();
        Set<RawAssetModel> result = assertDoesNotThrow(() -> apiResponse.get(5, TimeUnit.SECONDS));

        assertNotNull(apiResponse);
        assertNotNull(result);
        assertEquals(100, result.size(), "Set size should be 100");
    }

    @Test
    void fetchMarkets_ReturnsCompletedFuture_OfMarketModels() {
        CompletableFuture<Set<RawMarketModel>> apiResponse = dataAsyncService.fetchMarkets();
        Set<RawMarketModel> result = assertDoesNotThrow(() -> apiResponse.get(5, TimeUnit.SECONDS));

        assertNotNull(apiResponse);
        assertNotNull(result);
        assertEquals(100, result.size(), "Set size should be 100");
    }

    @Test
    void asyncFetch_Completes_AllAsyncTasks() {
        CompletableFuture<Void> asyncTasks = dataAsyncService.asyncFetch();

        assertNotNull(asyncTasks);
        assertDoesNotThrow(() -> asyncTasks.get(5, TimeUnit.SECONDS));
        assertTrue(asyncTasks.isDone());
    }
}
