package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawAssetModelRepository;
import com.example.data_ingestion_service.repository.RawExchangeModelRepository;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DatabaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DatabaseServiceTest {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    RawAssetModelRepository assetModelRepository;

    @Autowired
    RawExchangeModelRepository exchangeModelRepository;

    @Autowired
    RawMarketModelRepository marketModelRepository;

    RawAssetModel assetModel = RawAssetModel.builder()
            .modelId(UUID.randomUUID())
            .id("asset-id-123")
            .rank(1)
            .symbol("SYM")
            .name("Sample Asset")
            .supply(BigDecimal.valueOf(1000000.00))
            .maxSupply(BigDecimal.valueOf(2000000.00))
            .marketCapUsd(BigDecimal.valueOf(500000000.00))
            .volumeUsd24Hr(BigDecimal.valueOf(25000000.00))
            .priceUsd(BigDecimal.valueOf(50.12345678))
            .changePercent24Hr(BigDecimal.valueOf(-0.15))
            .vwap24Hr(BigDecimal.valueOf(50.98765432))
            .explorer("https://www.example.com/explorer")
            .timestamp(Instant.now().toEpochMilli())
            .createdAt(Instant.now())
            .build();

    RawExchangesModel exchangesModel = RawExchangesModel.builder()
            .modelId(UUID.randomUUID())
            .exchangeId("exchange-id-123")
            .name("Sample Exchange")
            .rank(1)
            .percentTotalVolume(BigDecimal.valueOf(12.3456))
            .volumeUsd(BigDecimal.valueOf(12345678.90))
            .tradingPairs(250)
            .socket(true)
            .exchangeUrl("https://www.example.com/exchange")
            .updated(Instant.now().toEpochMilli())
            .timestamp(Instant.now().toEpochMilli())
            .createdAt(Instant.now())
            .build();

    RawMarketModel marketModel = RawMarketModel.builder()
            .modelId(UUID.randomUUID())
            .exchangeId("exchange-id-456")
            .rank(1)
            .baseSymbol("BTC")
            .baseId("bitcoin")
            .quoteSymbol("USD")
            .quoteId("usd")
            .priceQuote(BigDecimal.valueOf(12345.67890123))
            .priceUsd(BigDecimal.valueOf(12345.67890123))
            .volumeUsd24Hr(BigDecimal.valueOf(123456789.12))
            .percentExchangeVolume(BigDecimal.valueOf(12.3456))
            .tradesCount24Hr(1000)
            .updated(Instant.now().toEpochMilli())
            .timestamp(Instant.now().toEpochMilli())
            .createdAt(Instant.now())
            .build();

    Set<RawAssetModel> assetModels = Set.of(assetModel);
    Set<RawExchangesModel> exchangesModels = Set.of(exchangesModel);
    Set<RawMarketModel> marketModels = Set.of(marketModel);
    Set<Object> models = Set.of(assetModels, exchangesModels, marketModels);

    @Test
    void saveToDatabase_IdentifiesEntity_SavesToCorrectDB() {
        assertDoesNotThrow(() -> databaseService.saveToDatabase(models));

        Set<RawAssetModel> fetchedAssets = (Set<RawAssetModel>) assetModelRepository.findAll();
        Set<RawExchangesModel> fetchedExchanges = (Set<RawExchangesModel>) exchangeModelRepository.findAll();
        Set<RawMarketModel> fetchedMarkets = (Set<RawMarketModel>) marketModelRepository.findAll();

        assertEquals(Set.of(assetModel), new HashSet<>(fetchedAssets));
        assertEquals(Set.of(exchangesModel), new HashSet<>(fetchedExchanges));
        assertEquals(Set.of(marketModel), new HashSet<>(fetchedMarkets));
    }
}
