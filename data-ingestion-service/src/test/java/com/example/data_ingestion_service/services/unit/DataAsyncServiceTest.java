package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.*;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.impl.DataAsyncServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class DataAsyncServiceTest {
    @Mock
    MarketService marketService;

    @Mock
    ExchangeService exchangeService;

    @Mock
    AssetService assetService;

    @Mock
    DatabaseService databaseService;

    @InjectMocks
    DataAsyncServiceImpl dataAsyncService;

    @Test
    void testAsyncFetch_SuccessfullyCompletesAllAsyncOperations() {
        // Arrange
        Set<RawExchangesModel> mockExchanges = Set.of(new RawExchangesModel());
        Set<RawAssetModel> mockAssets = Set.of(new RawAssetModel());
        Set<RawMarketModel> mockMarkets = Set.of(new RawMarketModel());

        // Mock dependent service methods
        when(exchangeService.convertToModel()).thenReturn(mockExchanges);
        when(assetService.convertToModel()).thenReturn(mockAssets);
        when(marketService.convertToModel()).thenReturn(mockMarkets);

        // Act
        CompletableFuture<Void> result = dataAsyncService.asyncFetch();

        // Assert
        assertDoesNotThrow(() -> result.get(), "Async fetch should not throw any exceptions");
        assertTrue(result.isDone(), "Async fetch should complete all tasks");

        // Verify
        verify(exchangeService).convertToModel();
        verify(assetService).convertToModel();
        verify(marketService).convertToModel();
    }

    @Test
    void testAsyncFetch_CompletedExceptionally() {
        // Arrange
        when(exchangeService.convertToModel()).thenThrow(new RuntimeException("Simulated exchange failure"));
        when(assetService.convertToModel()).thenReturn(Collections.emptySet());
        when(marketService.convertToModel()).thenReturn(Collections.emptySet());

        // Act & Assert
        CompletableFuture<Void> future = dataAsyncService.asyncFetch();

        future.exceptionally(ex -> {
            assert ex != null;
            assert ex.getMessage().contains("Failed to complete all async tasks");
            verify(exchangeService).convertToModel();
            verify(assetService).convertToModel();
            verify(marketService).convertToModel();
            verify(databaseService, times(0)).saveToDatabase(any()); // Ensure no save on failure
            log.error("Verified asyncFetch exception handling");
            return null;
        }).join();
    }
}
