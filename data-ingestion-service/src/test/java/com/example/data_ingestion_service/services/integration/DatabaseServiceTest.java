package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class DatabaseServiceTest {

    @Autowired
    private DatabaseService databaseService;

    @SpyBean
    private RawMarketModelRepository marketModelRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Set<RawMarketModel> testMarketModels;

    @BeforeEach
    void setUp() {
        marketModelRepository.deleteAll();

        testMarketModels = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            testMarketModels.add(RawMarketModel.builder()
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
                    .build());
        }
    }

    @Test
    @Transactional
    void shouldSaveMarketDataSuccessfully() throws DatabaseException {
        databaseService.saveToDatabase(testMarketModels);

        assertEquals(5, marketModelRepository.count());

        Set<String> savedExchangeIds = new HashSet<>();
        marketModelRepository.findAll().forEach(model -> savedExchangeIds.add(model.getExchangeId()));

        testMarketModels.forEach(model -> assertTrue(savedExchangeIds.contains(model.getExchangeId())));
    }

    @Test
    void shouldHandleConcurrentSaves() throws Exception {
        Set<RawMarketModel> batch1 = createTestBatch();
        Set<RawMarketModel> batch2 = createTestBatch();

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                databaseService.saveToDatabase(batch1);
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                databaseService.saveToDatabase(batch2);
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture.allOf(future1, future2).get(10, TimeUnit.SECONDS);

        assertEquals(batch1.size() + batch2.size(), marketModelRepository.count());
    }

    @Test
    void shouldRetryOnTransientErrors() throws Exception {
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    jdbcTemplate.execute("SELECT 1");
                }
            } catch (Exception ignored) {}
        });

        databaseService.saveToDatabase(testMarketModels);

        assertEquals(testMarketModels.size(), marketModelRepository.count());
    }

    @Test
    void activatesRecoverMethodAfterAllRetriesExhaust() {
        AtomicInteger attempts = new AtomicInteger(0);

        doAnswer(invocation -> {
            int attempt = attempts.incrementAndGet();
            System.out.println("Attempt number: " + attempt);
            throw new DataAccessException("Simulated database error - attempt " + attempt) {};
        }).when(marketModelRepository).saveAll(any());

        DatabaseException thrownException = assertThrows(DatabaseException.class, () -> databaseService.persistWithRetry(testMarketModels));

        System.out.println("Exception message: " + thrownException.getMessage());
        System.out.println("Exception cause: " + thrownException.getCause());

        verify(marketModelRepository, atLeast(2)).saveAll(any());
        assertTrue(attempts.get() >= 2,
                "Should have attempted multiple times, got: " + attempts.get());
    }

    private Set<RawMarketModel> createTestBatch() {
        Set<RawMarketModel> batch = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            batch.add(RawMarketModel.builder()
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
                    .build());
        }
        return batch;
    }
}