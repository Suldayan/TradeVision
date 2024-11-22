package com.example.data_ingestion_service;

import com.example.data_ingestion_service.client.ApiClient;
import com.example.data_ingestion_service.exception.CustomApiServiceException;
import com.example.data_ingestion_service.model.*;
import com.example.data_ingestion_service.service.ApiService;
import com.example.data_ingestion_service.service.FilterService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setup() {
        filterService = new FilterService();
        Timer timer = Timer.builder("test-api-response")
                .tags("test", "api")
                .publishPercentiles(0.95)
                .register(meterRegistry);
        apiService = new ApiService(apiClient, filterService, timer);
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void teardown() {
        filterService = null;
        apiService = null;
    }

    @Test
    void testGetMarketApiResponseAsBody_ReturnsMarketWrapperModel() throws CustomApiServiceException {
        // Arrange
        RawExchangesModel rawExchangesModel = RawExchangesModel.builder().build();
        RawAssetModel rawAssetModel = RawAssetModel.builder().build();
        RawMarketModel rawMarketModel = RawMarketModel.builder()
                .id(1L)
                .exchange(rawExchangesModel)
                .baseAsset(rawAssetModel)
                .quoteAsset(rawAssetModel)
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
        when(apiService.getMarketApiResponseAsBody()).thenReturn(rawMarketWrapperModel);
        // Act
        RawMarketWrapperModel result = apiService.getMarketApiResponseAsBody();

        // Assert
        assertNotNull(result);
        assertEquals(rawMarketWrapperModel, result);
        assertEquals(rawMarketWrapperModel.getMarketModelList().getFirst(), rawMarketModel);
    }
}
