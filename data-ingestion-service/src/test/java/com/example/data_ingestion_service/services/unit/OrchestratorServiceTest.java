package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.exceptions.OrchestratorException;
import com.example.data_ingestion_service.services.impl.OrchestratorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorServiceTest {

    @Mock
    MarketService marketService;

    @Mock
    DatabaseService databaseService;

    @InjectMocks
    OrchestratorServiceImpl orchestratorService;

    Market mockMarket1 = Market.builder()
            .exchangeId("BINANCE")
            .rank(1)
            .baseSymbol("BTC")
            .baseId("bitcoin")
            .quoteSymbol("USDT")
            .quoteId("tether")
            .priceQuote(new BigDecimal("30345.67"))
            .priceUsd(new BigDecimal("30340.50"))
            .volumeUsd24Hr(new BigDecimal("1450023456.78"))
            .percentExchangeVolume(new BigDecimal("22.75"))
            .tradesCount24Hr(124500)
            .updated(1717027200000L)
            .timestamp(1717027200000L)
            .build();

    Market mockMarket2 = Market.builder()
            .exchangeId("COINBASE")
            .rank(2)
            .baseSymbol("ETH")
            .baseId("ethereum")
            .quoteSymbol("USD")
            .quoteId("usd")
            .priceQuote(new BigDecimal("1856.30"))
            .priceUsd(new BigDecimal("1856.25"))
            .volumeUsd24Hr(new BigDecimal("890456123.45"))
            .percentExchangeVolume(new BigDecimal("18.20"))
            .tradesCount24Hr(87650)
            .updated(1717023600000L)
            .timestamp(null)
            .build();

    Set<Market> mockMarketSet = Set.of(mockMarket1, mockMarket2);

    MarketWrapper mockMarketWrapper = MarketWrapper.builder()
            .markets(Set.of(mockMarket1, mockMarket2))
            .timestamp(1717027200000L)
            .build();

    RawMarketModel rawMarketModel1 = RawMarketModel.builder()
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

    RawMarketModel rawMarketModel2 = RawMarketModel.builder()
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

    Set<RawMarketModel> mockMarketModels = Set.of(rawMarketModel1, rawMarketModel2);

    @Test
    void executeDataPipeline_SuccessfullyCompletesFlow() {
        when(marketService.getMarketsData()).thenReturn(mockMarketWrapper);
        when(marketService.convertToModel(mockMarketSet)).thenReturn(mockMarketModels);

        assertDoesNotThrow(() -> orchestratorService.executeDataPipeline());

        verify(marketService).convertToModel(mockMarketSet);
        verify(databaseService).saveToDatabase(mockMarketModels);
    }
}
