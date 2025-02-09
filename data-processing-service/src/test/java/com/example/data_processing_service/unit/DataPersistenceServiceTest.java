package com.example.data_processing_service.unit;

import com.example.data_processing_service.dto.RawMarketDTO;
import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.exception.DatabaseException;
import com.example.data_processing_service.services.impl.DataPersistenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(DataPersistenceServiceImpl.class)
@ActiveProfiles("test")
public class DataPersistenceServiceTest {

    @Mock
    private MarketModelRepository marketModelRepository;

    @InjectMocks
    private DataPersistenceServiceImpl dataPersistenceService;

    private Set<MarketModel> validMarketModels;
    private Set<MarketModel> invalidMarketModels;

    private static final Long TIMESTAMP = 1737247412551L;
    private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(TIMESTAMP),
            ZoneOffset.UTC);

    @BeforeEach
    void setup() {
        validMarketModels = new HashSet<>();
        invalidMarketModels = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            MarketModel model = MarketModel.builder()
                    .id(UUID.randomUUID())
                    .baseId("BTC")
                    .priceUsd(new BigDecimal("45000.50"))
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .timestamp(ZONED_DATE_TIME)
                    .build();
            validMarketModels.add(model);
        }

        for (int i = 0; i < 5; i++) {
            MarketModel model = MarketModel.builder()
                    .id(UUID.randomUUID())
                    .baseId("BTC")
                    .priceUsd(new BigDecimal("45000.50"))
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .timestamp(ZONED_DATE_TIME)
                    .build();
            invalidMarketModels.add(model);
        }

        marketModelRepository.deleteAll();
    }

    @Test
    void saveToDatabase_SavesValidMarketSet() throws DatabaseException {
        dataPersistenceService.saveToDatabase(validMarketModels);

        assertEquals(100, marketModelRepository.count(), "Repository size should be equal to 100");
        MarketModel savedModel = marketModelRepository.findAll().getFirst();
        assertEquals("BTC", savedModel.getBaseId());
    }
}
