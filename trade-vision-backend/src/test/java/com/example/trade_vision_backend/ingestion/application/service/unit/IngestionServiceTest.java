package com.example.trade_vision_backend.ingestion.application.service.unit;

import com.example.trade_vision_backend.ingestion.application.management.IngestionManagement;
import com.example.trade_vision_backend.ingestion.application.service.IngestionServiceImpl;
import com.example.trade_vision_backend.ingestion.infrastructure.client.IngestionClient;
import com.example.trade_vision_backend.ingestion.infrastructure.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.infrastructure.dto.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngestionServiceTest {
    @Mock
    private IngestionClient ingestionClient;

    @Mock
    private IngestionManagement ingestionManagement;

    @InjectMocks
    private IngestionServiceImpl ingestionService;

    private static final Long TIMESTAMP = 123456789L;

    @Test
    void getMarketsData_ReturnsValidMarketWrapperDTO() {
        MarketWrapperDTO wrapperDTO = new MarketWrapperDTO(
                createValidMarketDTOs(),
                TIMESTAMP
        );

        when(ingestionClient.getMarkets()).thenReturn(wrapperDTO);

        MarketWrapperDTO result = assertDoesNotThrow(() ->
                ingestionClient.getMarkets());

        assertNotNull(result);
        assertFalse(result.markets().isEmpty());
        assertEquals(100, result.markets().size());
    }

    @Test
    void getMarketsData_ThrowsRestClientExceptionOnEmptyData() {
        MarketWrapperDTO wrapperDTO = new MarketWrapperDTO(
                Collections.emptySet(),
                TIMESTAMP
        );

        when(ingestionClient.getMarkets()).thenReturn(wrapperDTO);

        RestClientException exception = assertThrows(RestClientException.class,
                () -> ingestionService.getMarketsData());

        assertTrue(exception.getMessage().contains("Client failed to fetch market wrapper data"));
    }

    @Test
    void getMarketsData_ThrowsRestClientExceptionOnInvalidDataSize() {
        MarketWrapperDTO wrapperDTO = new MarketWrapperDTO(
                Set.of(new RawMarketDTO(
                        "binance",
                        1,
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
                )),
                TIMESTAMP
        );

        when(ingestionClient.getMarkets()).thenReturn(wrapperDTO);

        RestClientException exception = assertThrows(RestClientException.class,
                () -> ingestionService.getMarketsData());

        assertTrue(exception.getMessage().contains("Client failed to fetch market wrapper data"));
    }

    @Test
    void getMarketsData_ThrowsRestClientExceptionOnNullData() {
        MarketWrapperDTO wrapperDTO = new MarketWrapperDTO(null, TIMESTAMP);

        when(ingestionClient.getMarkets()).thenReturn(wrapperDTO);

        RestClientException exception = assertThrows(RestClientException.class,
                () -> ingestionService.getMarketsData());

        assertTrue(exception.getMessage().contains("An unexpected error occurred when fetching market data"));
    }

    @Test
    void convertWrapperDataToRecord_ReturnsValidRawMarketDTOSet() {
        MarketWrapperDTO wrapperDTO = new MarketWrapperDTO(createValidMarketDTOs(), TIMESTAMP);

        Set<RawMarketDTO> result = assertDoesNotThrow(
                () -> ingestionService.convertWrapperDataToRecord(wrapperDTO));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(100, result.size());
    }

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

        when(ingestionClient.getMarkets()).thenReturn(wrapperDTO);

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
