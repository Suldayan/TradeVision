package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.impl.MarketServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@EnableRetry
public class MarketServiceTest {

    @Mock
    private MarketClient marketClient; // Mock the MarketClient

    @InjectMocks
    private MarketServiceImpl marketService; // Inject the mock into the service

    public static final Market market1 = new Market(
            "Binance",
            1,
            "BTC",
            "ethereum",
            "USDT",
            "USDT",
            new BigDecimal("45000.50"),
            new BigDecimal("45000.50"),
            new BigDecimal("300000000.00"),
            new BigDecimal("0.5"),
            100000,
            System.currentTimeMillis(),
            1737247412551L
    );

    @Test
    public void testGetMarketsData_Success() {
        // Arrange
        MarketWrapper expectedMarketWrapper = new MarketWrapper(Set.of(market1), 12124324L);
        when(marketClient.getMarkets()).thenReturn(expectedMarketWrapper);

        // Act
        MarketWrapper result = marketService.getMarketsData();

        // Assert
        assertNotNull(result);
        assertEquals(expectedMarketWrapper, result);
        verify(marketClient, times(1)).getMarkets(); // Verify the client was called once
    }

    @Test
    public void testGetMarketsData_RetryOnFailure() {
        // Arrange
        ApiException apiException = new ApiException("Simulated API failure");
        when(marketClient.getMarkets())
                .thenThrow(apiException) // First call fails
                .thenThrow(apiException) // Second call fails
                .thenReturn(new MarketWrapper(Set.of(market1), 12234345L)); // Third call succeeds

        // Act & Assert
        assertThrows(ApiException.class, () -> marketService.getMarketsData());

        // Verify the retry mechanism
        verify(marketClient, times(3)).getMarkets(); // Verify the client was called 3 times (initial + 2 retries)
    }

    @Test
    public void testGetMarketsData_RetryExhausted() {
        // Arrange
        ApiException apiException = new ApiException("Simulated API failure");
        when(marketClient.getMarkets()).thenThrow(apiException); // Always fail

        // Act & Assert
        assertThrows(ApiException.class, () -> marketService.getMarketsData());

        // Verify the retry mechanism
        verify(marketClient, times(3)).getMarkets(); // Verify the client was called 3 times (initial + 2 retries)
    }
}
