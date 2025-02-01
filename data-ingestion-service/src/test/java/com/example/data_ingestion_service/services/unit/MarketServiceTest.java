package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.impl.MarketServiceImpl;
import com.example.data_ingestion_service.services.mapper.MarketMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketServiceTest {

    @Mock
    MarketClient marketClient;

    @Mock
    MarketMapper marketMapper;

    @InjectMocks
    MarketServiceImpl marketService;

    public static final RawMarketModel rawMarketModel1 = RawMarketModel.builder()
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

    public static final RawMarketModel rawMarketModel2 = RawMarketModel.builder()
            .modelId(UUID.randomUUID())
            .baseId("ETH")
            .rank(2)
            .priceQuote(new BigDecimal("3000.75"))
            .priceUsd(new BigDecimal("3000.75"))
            .volumeUsd24Hr(new BigDecimal("150000000.00"))
            .percentExchangeVolume(new BigDecimal("0.7"))
            .tradesCount24Hr(50000)
            .updated(System.currentTimeMillis())
            .exchangeId("Coinbase")
            .quoteId("USDT")
            .baseSymbol("ETH")
            .quoteSymbol("USDT")
            .timestamp(1737247412551L)
            .build();

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

    public static final Market market2 = new Market(
            "Coinbase",
            2,
            "ETH",
            "ethereum",
            "USDT",
            "USDT",
            new BigDecimal("3000.75"),
            new BigDecimal("3000.75"),
            new BigDecimal("150000000.00"),
            new BigDecimal("0.7"),
            50000,
            System.currentTimeMillis(),
            1737247412551L
    );

    public static final MarketWrapper mockMarketWrapper = new MarketWrapper(Set.of(market1, market2), 1737247412551L);

    @Test
    void testGetMarketData_ReturnsValid_SetOfMarketRecords() {
        when(marketClient.getMarkets()).thenReturn(mockMarketWrapper);

        MarketWrapper result = assertDoesNotThrow(() -> marketService.getMarketsData());

        assertNotNull(result);
        assertEquals(2, result.markets().size());
        assertEquals(1737247412551L, result.timestamp());
        verify(marketClient, times(1)).getMarkets();
    }

    @Test
    void getMarketData_ThrowsException_OnClientError() {
        when(marketClient.getMarkets()).thenThrow(new RuntimeException("Client Error"));

        ApiException exception = assertThrows(ApiException.class, () -> marketService.getMarketsData());
        assertTrue(exception.getMessage().contains("Failed to fetch market wrapper data"));
        verify(marketClient).getMarkets();
    }

    @Test
    void getMarketData_ThrowsException_WhenMarketsEmpty() {
        MarketWrapper emptyWrapper = new MarketWrapper(Collections.emptySet(), 1737247412551L);
        when(marketClient.getMarkets()).thenReturn(emptyWrapper);

        ApiException exception = assertThrows(ApiException.class, () -> marketService.getMarketsData());
        assertEquals("Market set from wrapper returned empty", exception.getMessage());
        verify(marketClient).getMarkets();
    }

    @Test
    void getMarketData_ThrowsException_WhenMarketContainsNull() {
        Set<Market> marketsWithNull = new HashSet<>();
        marketsWithNull.add(market1);
        marketsWithNull.add(null);
        MarketWrapper wrapperWithNull = new MarketWrapper(marketsWithNull, 1737247412551L);
        when(marketClient.getMarkets()).thenReturn(wrapperWithNull);

        ApiException exception = assertThrows(ApiException.class, () -> marketService.getMarketsData());
        assertEquals("An object in the market set has returned as null", exception.getMessage());
        verify(marketClient).getMarkets();
    }

    @Test
    void testConvertWrapperDataToRecord_Success() {
        when(marketClient.getMarkets()).thenReturn(mockMarketWrapper);

        Set<Market> result = marketService.convertWrapperDataToRecord();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(market1));
        assertTrue(result.contains(market2));
        verify(marketClient).getMarkets();
    }

    @Test
    void testConvertWrapperDataToRecord_ThrowsException_WhenEmpty() {
        MarketWrapper emptyWrapper = new MarketWrapper(Collections.emptySet(), 1737247412551L);
        when(marketClient.getMarkets()).thenReturn(emptyWrapper);

        ApiException exception = assertThrows(ApiException.class,
                () -> marketService.convertWrapperDataToRecord());
        assertEquals("Market set from wrapper returned empty", exception.getMessage());
    }

    @Test
    void convertToModel_ReturnsValid_RawMarketModelSet() {
        when(marketClient.getMarkets()).thenReturn(mockMarketWrapper);
        when(marketMapper.marketRecordToEntity(anySet())).thenReturn(Set.of(rawMarketModel1, rawMarketModel2));

        Set<RawMarketModel> result = marketService.convertToModel();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(rawMarketModel1));
        assertTrue(result.contains(rawMarketModel2));

        verify(marketClient).getMarkets();
        verify(marketMapper).marketRecordToEntity(anySet());
    }

    @Test
    void convertToModel_PropagatesException_FromGetMarketsData() {
        when(marketClient.getMarkets()).thenThrow(new ApiException("Test error"));

        assertThrows(ApiException.class, () -> marketService.convertToModel());
        verify(marketClient).getMarkets();
        verify(marketMapper, never()).marketRecordToEntity(anySet());
    }
}
