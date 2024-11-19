package com.example.data_ingestion_service;

import com.example.data_ingestion_service.client.ApiClient;
import com.example.data_ingestion_service.exception.CustomApiServiceException;
import com.example.data_ingestion_service.model.*;
import com.example.data_ingestion_service.service.ApiService;
import com.example.data_ingestion_service.service.FilterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ApiServiceUnitTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private FilterService filterService;

    @InjectMocks
    private ApiService apiService;

    @BeforeEach
    void setup() {
        filterService = new FilterService();
        apiService = new ApiService(apiClient, filterService);
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void teardown() {
        filterService = null;
        apiService = null;
    }

    @Test
    void testFetchMarketData_ReturnsMarketWrapperObject() throws CustomApiServiceException {
        // Arrange
        RawMarketModel rawMarketModel = RawMarketModel.builder()
                .id(1L)
                .baseSymbol("BTC")
                .quoteSymbol("USD")
                .priceUsd(new BigDecimal("45000.50"))
                .volumeUsd24Hr(new BigDecimal("1000000000.00"))
                .percentExchangeVolume(new BigDecimal("0.05"))
                .updated(1633024800000L)
                .build();

        RawMarketWrapperModel rawMarketWrapperModel = RawMarketWrapperModel.builder()
                .marketModelList(Collections.singletonList(rawMarketModel))
                .build();

        // Mock
        when(apiService.fetchMarketData()).thenReturn(ResponseEntity.ok(rawMarketWrapperModel));

        // Act
        RawMarketWrapperModel result = apiService.fetchMarketData().getBody();

        // Assert
        assertNotNull(result);
        assertEquals(rawMarketWrapperModel, result);
        assertEquals(rawMarketWrapperModel.getMarketModelList().getFirst(), rawMarketModel);
    }

    @Test
    void testSendDataToFilter_Success() throws CustomApiServiceException, InterruptedException {
        // Arrange :(
        RawMarketModel rawMarketModel = new RawMarketModel();
        RawExchangesModel rawExchangesModel = new RawExchangesModel();
        RawAssetModel rawAssetModel = new RawAssetModel();

        RawMarketWrapperModel rawMarketWrapperModel = RawMarketWrapperModel.builder()
                .marketModelList(Collections.singletonList(rawMarketModel)).build();

        RawExchangeWrapperModel rawExchangeWrapperModel = RawExchangeWrapperModel.builder()
                .rawExchangesModelList(Collections.singletonList(rawExchangesModel))
                .build();

        RawAssetWrapperModel rawAssetWrapperModel = RawAssetWrapperModel.builder()
                .rawAssetModelList(Collections.singletonList(rawAssetModel))
                .build();

        // Act :/
        when(apiService.fetchMarketData()).thenReturn(ResponseEntity.ok(rawMarketWrapperModel));
        when(apiService.fetchExchangeData()).thenReturn(ResponseEntity.ok(rawExchangeWrapperModel));
        when(apiService.fetchAssetData()).thenReturn(ResponseEntity.ok(rawAssetWrapperModel));

        apiService.sendDataToFilter();

        Thread.sleep(100);

        verify(filterService, times(1)).processMarketData(rawMarketWrapperModel);
        verify(filterService, times(1)).processExchangeData(rawExchangeWrapperModel);
        verify(filterService, times(1)).processAssetHistoryData(rawAssetWrapperModel);
    }
}
