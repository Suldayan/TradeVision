package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.services.DataAsyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataAsyncServiceTest {

    @Autowired
    DataAsyncService dataAsyncService;

    @Test
    void testFetchAssets_ReturnsSetOfRawAssetModelFutures() {
        CompletableFuture<Set<RawAssetModel>> assetFutures = dataAsyncService.fetchAssets();

        assertDoesNotThrow(() -> assetFutures);
        assertFalse(assetFutures.isCompletedExceptionally());

        assetFutures.whenComplete((result, ex) -> {
            assertEquals(100, result.size(), "Asset future set size should be 100");
            assertNotNull(result);
            assertNull(ex);
            assertFalse(result.isEmpty());
        });
    }

    @Test
    void testAsyncFetch_SuccessfullyCompletesAllFutures() {
        assertDoesNotThrow(() -> dataAsyncService.asyncFetch());
        assertFalse(dataAsyncService.asyncFetch().isCompletedExceptionally());
    }
}
