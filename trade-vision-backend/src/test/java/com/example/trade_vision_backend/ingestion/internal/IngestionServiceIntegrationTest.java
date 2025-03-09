package com.example.trade_vision_backend.ingestion.internal;

import com.example.trade_vision_backend.ingestion.internal.infrastructure.repository.IngestionRepository;
import com.example.trade_vision_backend.ingestion.internal.infrastructure.service.IngestionService;
import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class IngestionServiceIntegrationTest {

    @Autowired
    private IngestionRepository repository;

    @Autowired
    private IngestionService ingestionService;

    @Test
    public void executeIngestion_SuccessfullyExecutesEntireFlow() {
        assertDoesNotThrow(() -> ingestionService.executeIngestion());
    }

    @Transactional
    @Test
    public void executeIngestion_SuccessfullyExecutesEntireFlowAndUpdatesDataOnExistingData() {
        List<RawMarketModel> dummyData = generateDummyRawMarketModels();
        repository.saveAll(dummyData);
        List<RawMarketModel> oldData = repository.findAll();

        assertDoesNotThrow(() -> ingestionService.executeIngestion());

        List<RawMarketModel> updatedData = repository.findAll();

        assertEquals(100, oldData.size());
        assertEquals(100, updatedData.size());
        assertNotEquals(oldData, updatedData);
    }

    private List<RawMarketModel> generateDummyRawMarketModels() {
        List<RawMarketModel> models = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            RawMarketModel model = RawMarketModel.builder()
                    .id(UUID.randomUUID())
                    .exchangeId("exchange-" + i)
                    .rank(i)
                    .baseSymbol("BASE" + i)
                    .baseId("baseId-" + i)
                    .quoteSymbol("QUOTE" + i)
                    .quoteId("quoteId-" + i)
                    .priceQuote(BigDecimal.valueOf(10.0 * i))
                    .priceUsd(BigDecimal.valueOf(10.0 * i + 1))
                    .volumeUsd24Hr(BigDecimal.valueOf(1000.0 * i))
                    .percentExchangeVolume(BigDecimal.valueOf(5.0 * i))
                    .tradesCount24Hr(i * 2)
                    .updated(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            models.add(model);
        }
        return models;
    }
}
