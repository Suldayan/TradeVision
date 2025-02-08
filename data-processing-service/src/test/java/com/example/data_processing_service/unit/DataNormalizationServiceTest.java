package com.example.data_processing_service.unit;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.impl.DataNormalizationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DataNormalizationServiceTest {

    @InjectMocks
    DataNormalizationServiceImpl dataNormalizationService;

    private static final RawMarketModel mockRawMarketModel = RawMarketModel.builder().build();

    private static final Long mockTimestamp = 123456789L;

    @Test
    void transformToModel_SuccessfullyTransforms() throws IllegalArgumentException {
        Set<MarketModel> result = assertDoesNotThrow(() ->
                dataNormalizationService.transformToMarketModel(createRawMarketModelSet(), mockTimestamp));

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(100, result.size(), "Result size should be 100");
        verify(dataNormalizationService, times(1)).transformToMarketModel(createRawMarketModelSet(), mockTimestamp);
    }

    @Test
    void transformToModel_ThrowsValidationException_OnEmptySet() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dataNormalizationService.transformToMarketModel(Collections.emptySet(), mockTimestamp));

        assertTrue(exception.getMessage().contains("Unable to push data forward due to empty market set for timestamp"));
    }

    @Test
    void transformToModel_ThrowsValidationException_OnInvalidSetSize() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dataNormalizationService.transformToMarketModel(createInvalidRawMarketModelSet(), mockTimestamp));

        assertTrue(exception.getMessage().contains("Market models with timestamp: %s fetched but is missing data with size"));
    }

    private static Set<RawMarketModel> createRawMarketModelSet() {
        Set<RawMarketModel> rawMarketModelSet = new HashSet<>();
        for (int i = 0; i <= 100; i++) {
            rawMarketModelSet.add(mockRawMarketModel);
        }

        return rawMarketModelSet;
    }

    private static Set<RawMarketModel> createInvalidRawMarketModelSet() {
        Set<RawMarketModel> rawMarketModelSet = new HashSet<>();
        for (int i = 0; i <= 10; i++) {
            rawMarketModelSet.add(mockRawMarketModel);
        }

        return rawMarketModelSet;
    }
}
