package com.example.data_processing_service.integration;


import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.repository.MarketModelRepository;
import com.example.data_processing_service.database.service.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@SpringBootTest()
@Transactional
@ActiveProfiles("test")
public class DatabaseServiceTest {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private MarketModelRepository repository;

    private static final Set<MarketModel> validMarketModels = new HashSet<>();
    private static final Set<MarketModel> invalidMarketModels = new HashSet<>();

    @BeforeEach
    void setup() {
        repository.deleteAll();

        for (int i = 0; i < 100; i++) {
            MarketModel model = MarketModel.builder()
                    .id(UUID.randomUUID())
                    .baseId("BTC")
                    .priceUsd(new BigDecimal("45000.50"))
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .timestamp(ZonedDateTime.now())
                    .createdAt(Instant.now())
                    .build();
            validMarketModels.add(model);
        }

        for (int i = 0; i < 10; i++) {
            MarketModel model = MarketModel.builder()
                    .id(UUID.randomUUID())
                    .baseId("BTC")
                    .priceUsd(new BigDecimal("45000.50"))
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .timestamp(ZonedDateTime.now())
                    .createdAt(Instant.now())
                    .build();
            invalidMarketModels.add(model);
        }
    }

    @Test
    void saveToDatabase_ShouldSaveAllModels_Successfully() {
        assertDoesNotThrow(() -> databaseService.saveToDatabase(validMarketModels));

        assertEquals(100, repository.count(), "Count should be 100 as all models should be saved");
    }

    @Test
    void saveToDatabase_ShouldThrowIllegalArgumentException_OnMissingData() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> databaseService.saveToDatabase(invalidMarketModels));

        assertTrue(exception.getMessage().contains("Market model set passed but is missing data"));
        assertEquals(0, repository.count(), "Count should be 0. No models should be saved");
    }
}