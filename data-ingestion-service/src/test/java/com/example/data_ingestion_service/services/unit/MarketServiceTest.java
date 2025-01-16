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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarketServiceTest {

    @Mock
    MarketClient marketClient;

    @Mock
    MarketMapper marketMapper;

    @InjectMocks
    MarketServiceImpl marketService;

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
            System.currentTimeMillis()
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
            System.currentTimeMillis()
    );

    public static final MarketWrapper mockMarketWrapper = new MarketWrapper(Set.of(market1, market2), 12235235L);

    @Test
    void testGetMarketData_ReturnsValid_SetOfMarketRecords() {
        when(marketClient.getMarkets()).thenReturn(mockMarketWrapper);

        Set<Market> result = marketService.getMarketsData();

        assertDoesNotThrow(() -> marketService.getMarketsData());
        assertNotNull(result);
        assertTrue(result.contains(market1));
        assertTrue(result.contains(market2));
        assertEquals(2, result.size(), "Set size should be 2");
    }

    @Test
    void testGetMarketData_ThrowsException_OnNullData() {
        when(marketClient.getMarkets()).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> marketService.getMarketsData());
        assertTrue(exception.getMessage().contains("Markets data fetched but return as null"));
    }

    @Test
    void getMarketData_ThrowsException_OnEmptyDataSet() {
        MarketWrapper marketWrapper = new MarketWrapper(new HashSet<>(), 1232432L);
        when(marketClient.getMarkets()).thenReturn(marketWrapper);

        ApiException exception = assertThrows(ApiException.class, () -> marketService.getMarketsData());

        assertTrue(exception.getMessage().contains("Market set fetched but is empty"));
    }

    @Test
    void getMarketData_ThrowsException_OnClientError() {
        when(marketClient.getMarkets()).thenThrow(new RuntimeException("Client Error"));

        ApiException exception = assertThrows(ApiException.class, () -> marketService.getMarketsData());
        assertTrue(exception.getMessage().contains("Failed to fetch market wrapper data"));
        verify(marketClient).getMarkets();
    }

    @Test
    void convertToModel_ReturnsValid_RawMarketModelSet() {
        RawMarketModel rawMarketModel1 = RawMarketModel.builder()
                .modelId("1")
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
                .build();

        RawMarketModel rawMarketModel2 = RawMarketModel.builder()
                .modelId("2")
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
                .build();

        when(marketClient.getMarkets()).thenReturn(mockMarketWrapper);
        when(marketMapper.marketRecordToEntity(Set.of(market1, market2))).thenReturn(Set.of(rawMarketModel1, rawMarketModel2));

        Set<RawMarketModel> result = marketService.convertToModel();

        assertDoesNotThrow(() -> marketService.convertToModel());
        assertNotNull(result);
        assertEquals(2, result.size(), "Set size should be 2");
    }
}
