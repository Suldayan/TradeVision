package com.example.trade_vision_backend.ingestion.unit;

import com.example.trade_vision_backend.ingestion.IngestionManagement;
import com.example.trade_vision_backend.ingestion.internal.application.IngestionServiceImpl;
import com.example.trade_vision_backend.ingestion.market.MarketService;
import com.example.trade_vision_backend.ingestion.market.domain.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class IngestionServiceTest {

    @Mock
    private MarketService marketService;

    @Mock
    private IngestionManagement ingestionManagement;

    @InjectMocks
    private IngestionServiceImpl ingestionService;

    private static final Long TIMESTAMP = 123456789L;

    @Test
    void sendEvent_SuccessfullySendsEvent() {
        doNothing().when(ingestionManagement).complete(createValidMarketDTOs());

        assertDoesNotThrow(() -> ingestionService.sendEvent(createValidMarketDTOs()));

        verify(ingestionManagement, times(1)).complete(createValidMarketDTOs());
    }

    @Test
    void executeIngestion_SuccessfullyExecutesEntireIngestionFlow() {
        Set<RawMarketDTO> marketDTOS = createValidMarketDTOs();
        MarketWrapperDTO wrapperDTO = new MarketWrapperDTO(marketDTOS, TIMESTAMP);

        when(marketService.getMarketsData()).thenReturn(wrapperDTO);

        assertDoesNotThrow(() -> ingestionService.executeIngestion());

        verify(ingestionManagement, times(1)).complete(anySet());
    }

    private static Set<RawMarketDTO> createValidMarketDTOs() {
        Set<RawMarketDTO> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            set.add(new RawMarketDTO(
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00"),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200,
                    1696252800000L,
                    null
            ));
        }

        return set;
    }
}
