package com.example.data_processing_service.unit;

import com.example.data_processing_service.client.IngestionClient;
import com.example.data_processing_service.dto.RawMarketDTO;
import com.example.data_processing_service.services.impl.IngestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngestionServiceTest {

    @Mock
    private IngestionClient ingestionClient;

    @InjectMocks
    private IngestionServiceImpl ingestionService;

    private Set<RawMarketDTO> validMarketModels;
    private Set<RawMarketDTO> invalidMarketModels;

    private static final Long TIMESTAMP = 1737247412551L;

    @BeforeEach
    void setup() {
        validMarketModels = new HashSet<>();
        invalidMarketModels = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            RawMarketDTO model = RawMarketDTO.builder()
                    .baseId("BTC")
                    .rank(1)
                    .priceQuote(new BigDecimal("45000.50"))
                    .priceUsd(new BigDecimal("45000.50"))
                    .volumeUsd24Hr(new BigDecimal("300000000.00"))
                    .percentExchangeVolume(new BigDecimal("0.5"))
                    .tradesCount24Hr(100000)
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .baseSymbol("BTC")
                    .quoteSymbol("USDT")
                    .timestamp(TIMESTAMP)
                    .build();
            validMarketModels.add(model);
        }

        for (int i = 0; i < 5; i++) {
            RawMarketDTO model = RawMarketDTO.builder()
                    .baseId("BTC")
                    .rank(1)
                    .priceQuote(new BigDecimal("45000.50"))
                    .priceUsd(new BigDecimal("45000.50"))
                    .volumeUsd24Hr(new BigDecimal("300000000.00"))
                    .percentExchangeVolume(new BigDecimal("0.5"))
                    .tradesCount24Hr(100000)
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .baseSymbol("BTC")
                    .quoteSymbol("USDT")
                    .timestamp(TIMESTAMP)
                    .build();
            invalidMarketModels.add(model);
        }
    }

    @Test
    void fetchRawMarkets_SuccessfullyFetchesAllMarkets() {
        when(ingestionClient.getRawMarketModels(TIMESTAMP)).thenReturn(validMarketModels);

        Set<RawMarketDTO> result = assertDoesNotThrow(() -> ingestionService.fetchRawMarkets(TIMESTAMP));

        assertNotNull(result, "Result should not be null");
        assertEquals(100, result.size(), "Result size should be 100");

        verify(ingestionClient, times(1)).getRawMarketModels(TIMESTAMP);
    }

    @Test
    void fetchRawMarkets_ThrowsIllegalArgumentException_OnEmptySet() {
        when(ingestionClient.getRawMarketModels(TIMESTAMP)).thenReturn(Collections.emptySet());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ingestionService.fetchRawMarkets(TIMESTAMP));

        assertEquals("Received empty market set from ingestion service. This might indicate an API issue", exception.getMessage());
    }

    @Test
    void fetchRawMarkets_ThrowsIllegalArgumentException_OnMissingData() {
        when(ingestionClient.getRawMarketModels(TIMESTAMP)).thenReturn(invalidMarketModels);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ingestionService.fetchRawMarkets(TIMESTAMP));

        assertTrue(exception.getMessage().contains("This indicates incomplete data from the data-ingestion microservice"));
    }
}
