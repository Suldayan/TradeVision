package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.impl.OrchestratorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorServiceTest {

    @Mock
    MarketService marketService;

    @Mock
    DatabaseService databaseService;

    @InjectMocks
    OrchestratorServiceImpl orchestratorService;

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
        when(marketService.convertToModel()).thenReturn(mockMarketModels);

        assertDoesNotThrow(() -> orchestratorService.executeDataPipeline());

        verify(marketService).convertToModel();
        verify(databaseService).saveToDatabase(mockMarketModels);
    }
}
