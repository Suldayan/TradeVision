package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.*;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.AsyncException;
import com.example.data_ingestion_service.services.exceptions.ValidationException;
import com.example.data_ingestion_service.services.impl.DataAsyncServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void fetchMarket_Success() throws ExecutionException, InterruptedException {
        Set<RawMarketModel> mockMarkets = Set.of(new RawMarketModel());
        when(marketService.convertToModel()).thenReturn(mockMarkets);

        CompletableFuture<Set<RawMarketModel>> result = dataAsyncService.fetchMarkets();
        Set<RawMarketModel> markets = result.get();

        assertNotNull(markets);
        assertEquals(mockMarkets, markets);
        verify(marketService).convertToModel();
    }

    @Test
    void fetchMarkets_ThrowsException() {
        when(marketService.convertToModel()).thenThrow(new RuntimeException("API Error"));

        CompletableFuture<Set<RawMarketModel>> future = dataAsyncService.fetchMarkets();
        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertInstanceOf(AsyncException.class, exception.getCause());
        assertEquals("Market fetch has failed on the asynchronous flow", exception.getCause().getMessage());
    }

    @Test
    void fetchMarkets_ThrowsException_OnEmptySet() {
        when(marketService.convertToModel()).thenReturn(Collections.emptySet());
        CompletableFuture<Set<RawMarketModel>> future = dataAsyncService.fetchMarkets();

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ValidationException.class, exception.getCause());
    }

    @Test
    void fetchExchanges_Success() throws ExecutionException, InterruptedException {
        Set<RawExchangesModel> mockExchanges = Set.of(new RawExchangesModel());
        when(exchangeService.convertToModel()).thenReturn(mockExchanges);

        CompletableFuture<Set<RawExchangesModel>> result = dataAsyncService.fetchExchanges();
        Set<RawExchangesModel> exchanges = result.get();

        assertNotNull(exchanges);
        assertEquals(mockExchanges, exchanges);
        verify(exchangeService).convertToModel();
    }

    @Test
    void fetchExchanges_ThrowsException() {
        when(exchangeService.convertToModel()).thenThrow(new RuntimeException("API Error"));

        CompletableFuture<Set<RawExchangesModel>> future = dataAsyncService.fetchExchanges();
        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertInstanceOf(AsyncException.class, exception.getCause());
        assertEquals("Exchange fetch has failed on the asynchronous flow", exception.getCause().getMessage());
    }

    @Test
    void fetchExchanges_ThrowsException_OnEmptySet() {
        when(exchangeService.convertToModel()).thenReturn(Collections.emptySet());
        CompletableFuture<Set<RawExchangesModel>> future = dataAsyncService.fetchExchanges();

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ValidationException.class, exception.getCause());
    }

    @Test
    void fetchAssets_Success() throws ExecutionException, InterruptedException {
        Set<RawAssetModel> mockAssets = Set.of(new RawAssetModel());
        when(assetService.convertToModel()).thenReturn(mockAssets);

        CompletableFuture<Set<RawAssetModel>> result = dataAsyncService.fetchAssets();
        Set<RawAssetModel> assets = result.get();

        assertNotNull(assets);
        assertEquals(mockAssets, assets);
        verify(assetService).convertToModel();
    }

    @Test
    void fetchAssets_ThrowsException() {
        when(assetService.convertToModel()).thenThrow(new RuntimeException("API Error"));

        CompletableFuture<Set<RawAssetModel>> future = dataAsyncService.fetchAssets();
        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertInstanceOf(AsyncException.class, exception.getCause());
        assertEquals("Asset fetch has failed on the asynchronous flow", exception.getCause().getMessage());
    }

    @Test
    void fetchAssets_ThrowsException_OnEmptySet() {
        when(assetService.convertToModel()).thenReturn(Collections.emptySet());
        CompletableFuture<Set<RawAssetModel>> future = dataAsyncService.fetchAssets();

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ValidationException.class, exception.getCause());
    }

    @Test
    void asyncFetch_SuccessfullyCompletes_AllAsyncOperations() throws ExecutionException, InterruptedException {
        Set<RawExchangesModel> mockExchanges = Set.of(new RawExchangesModel());
        Set<RawAssetModel> mockAssets = Set.of(new RawAssetModel());
        Set<RawMarketModel> mockMarkets = Set.of(new RawMarketModel());

        when(exchangeService.convertToModel()).thenReturn(mockExchanges);
        when(assetService.convertToModel()).thenReturn(mockAssets);
        when(marketService.convertToModel()).thenReturn(mockMarkets);
        doNothing().when(databaseService).saveToDatabase(any());

        CompletableFuture<Void> result = dataAsyncService.asyncFetch();
        result.get();

        assertTrue(result.isDone(), "Async fetch should complete all tasks");
        verify(exchangeService).convertToModel();
        verify(assetService).convertToModel();
        verify(marketService).convertToModel();
        verify(databaseService, times(3)).saveToDatabase(any());
    }

    @Test
    void asyncFetch_FailsWhenExchangeServiceFails() {
        when(exchangeService.convertToModel()).thenThrow(new RuntimeException("Exchange service failed"));
        when(assetService.convertToModel()).thenReturn(Collections.emptySet());
        when(marketService.convertToModel()).thenReturn(Collections.emptySet());

        CompletableFuture<Void> future = dataAsyncService.asyncFetch();
        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertInstanceOf(AsyncException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Async fetch has failed"));
    }
}