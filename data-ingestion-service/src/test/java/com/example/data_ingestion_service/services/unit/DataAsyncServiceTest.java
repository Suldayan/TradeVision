package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataAsyncServiceTest {
    @MockBean
    MarketService marketService;

    @MockBean
    ExchangeService exchangeService;

    @MockBean
    AssetService assetService;

    @Mock
    DatabaseService databaseService;

    @InjectMocks
    DataAsyncService dataAsyncService;

    @Test
    void testAsyncFetch_SuccessfullyCompletesAllAsyncOperations() {
        // TODO configure completable future set returns for each data type, mock the responses, and doReturn() with the async operation of those values
        doNothing().when(dataAsyncService.asyncFetch());
        assertDoesNotThrow(() -> dataAsyncService.asyncFetch(), "Async fetch should successfully complete");
    }
}
