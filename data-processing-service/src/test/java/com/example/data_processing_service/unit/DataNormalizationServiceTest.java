package com.example.data_processing_service.unit;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.impl.DataNormalizationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DataNormalizationServiceTest {

    @InjectMocks
    private DataNormalizationServiceImpl dataNormalizationService;

    private static final Long mockTimestamp = 123456789L;

    private static final String EXPECTED_EXCEPTION_MESSAGE =
            "Failed to transform model from raw to processed for timestamped data: 123456789";

    private Set<RawMarketModel> validMarketModels;
    private Set<RawMarketModel> invalidMarketModels;

    @BeforeEach
    void setup() {
        validMarketModels = new HashSet<>();
        invalidMarketModels = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            RawMarketModel model = RawMarketModel.builder()
                    .modelId(UUID.randomUUID())
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
                    .timestamp(1737247412551L)
                    .build();
            validMarketModels.add(model);
        }
    }

    @Test
    void transformToModel_SuccessfullyTransformsAllModels() {
        Set<MarketModel> result = assertDoesNotThrow(() ->
                dataNormalizationService.transformToMarketModel(validMarketModels, mockTimestamp));

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(100, result.size(), "Result size should be 100");
    }

    @Test
    void transformToModel_ThrowsValidationException_OnEmptySet() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dataNormalizationService.transformToMarketModel(Collections.emptySet(), mockTimestamp));

        assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    void transformToModel_ThrowsValidationException_OnInvalidSetSize() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dataNormalizationService.transformToMarketModel(invalidMarketModels, mockTimestamp));

        assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getMessage());
    }
}
