package com.example.trade_vision_backend.processing.internal;

import com.example.trade_vision_backend.ingestion.market.RawMarketDTO;
import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import com.example.trade_vision_backend.processing.ProcessedMarketModel;
import com.example.trade_vision_backend.processing.internal.infrastructure.db.ProcessingRepository;
import com.example.trade_vision_backend.processing.internal.infrastructure.exception.ProcessingException;
import com.example.trade_vision_backend.processing.internal.infrastructure.service.ProcessingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProcessingServiceUnitTest {

    @Mock
    private ProcessingRepository repository;

    @InjectMocks
    private ProcessingServiceImpl processingService;

    private static final Long MOCK_TIMESTAMP = 123456789L;

    @Test
    public void transformToMarketModel_SuccessfullyReturnsMarketModelSet() {
        Set<RawMarketModel> rawMarketModels = createValidMarketModels();

        Set<ProcessedMarketModel> result = assertDoesNotThrow(
                () -> processingService.transformToMarketModel(rawMarketModels, MOCK_TIMESTAMP));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(100, result.size());

        for (ProcessedMarketModel model : result) {
            assertNotNull(model.getTimestamp());
        }
    }

    @Transactional
    @Test
    public void executeProcessing_SuccessfullyExecutesFullProcessingFlow() {
        Set<RawMarketModel> validSet = createValidMarketModels();

        assertDoesNotThrow(
                () -> processingService.executeProcessing(validSet, MOCK_TIMESTAMP));
    }

    @Test
    public void executeProcessing_ThrowsProcessingExceptionOnEmptyData() {
        ProcessingException exception = assertThrows(
                ProcessingException.class, () -> processingService.executeProcessing(Collections.emptySet(), MOCK_TIMESTAMP));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Failed to process due to invalid data"));
    }

    @Test
    public void executeProcessing_ThrowsProcessingExceptionOnInvalidDataSize() {
        Set<RawMarketModel> invalidSet = createInvalidMarketModels();

        ProcessingException exception = assertThrows(
                ProcessingException.class, () -> processingService.executeProcessing(invalidSet, MOCK_TIMESTAMP));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Failed to process due to invalid data"));
    }

    private static Set<RawMarketModel> createValidMarketModels() {
        Set<RawMarketModel> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            set.add(new RawMarketModel(
                    UUID.randomUUID(),
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00").add(new BigDecimal(i)),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200 + i,
                    1696252800000L + i,
                    null
            ));
        }

        return set;
    }

    private static Set<RawMarketModel> createInvalidMarketModels() {
        Set<RawMarketModel> set = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            set.add(new RawMarketModel(
                    UUID.randomUUID(),
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00").add(new BigDecimal(i)),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200 + i,
                    1696252800000L + i,
                    null
            ));
        }

        return set;
    }
}
