package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import com.example.data_ingestion_service.services.impl.DatabaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(DatabaseServiceImpl.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DatabaseServiceTest {

    @Autowired
    private DatabaseServiceImpl databaseService;

    @Autowired
    private RawMarketModelRepository marketModelRepository;

    private Set<RawMarketModel> validMarketModels;

    @BeforeEach
    void setUp() {
        marketModelRepository.deleteAll();

        validMarketModels = new HashSet<>();
        RawMarketModel model = RawMarketModel.builder()
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
        validMarketModels.add(model);
    }

    @Test
    void saveToDatabase_WithValidData_ShouldSaveSuccessfully() throws DatabaseException {
        databaseService.saveToDatabase(validMarketModels);

        assertEquals(1, marketModelRepository.count());
        RawMarketModel savedModel = marketModelRepository.findAll().getFirst();
        assertEquals("BTC", savedModel.getBaseId());
    }

    @Test
    void saveToDatabase_WithEmptySet_ShouldThrowIllegalArgumentException() {
        Set<RawMarketModel> emptySet = new HashSet<>();

        assertThrows(IllegalArgumentException.class, () -> databaseService.saveToDatabase(emptySet));
    }

    @Test
    void saveToDatabase_WithNullEntry_ShouldThrowIllegalArgumentException() {
        Set<RawMarketModel> modelsWithNull = new HashSet<>();
        modelsWithNull.add(null);
        modelsWithNull.add(validMarketModels.iterator().next());

        assertThrows(IllegalArgumentException.class, () -> databaseService.saveToDatabase(modelsWithNull));
    }

    @Test
    void persistWithRetry_WhenDataAccessExceptionOccurs_ShouldRetryAndEventuallyThrowDatabaseException() {
        RawMarketModelRepository mockRepository = mock(RawMarketModelRepository.class);
        DatabaseServiceImpl serviceWithMock = new DatabaseServiceImpl(mockRepository);

        when(mockRepository.saveAll(any())).thenThrow(new DataAccessException("Test exception") {});

        assertThrows(DatabaseException.class, () -> serviceWithMock.persistWithRetry(validMarketModels));

        verify(mockRepository, atLeastOnce()).saveAll(validMarketModels);
    }

    @Test
    void persistWithRetry_WhenUnexpectedRuntimeException_ShouldThrowDatabaseException() {
        RawMarketModelRepository mockRepository = mock(RawMarketModelRepository.class);
        DatabaseServiceImpl serviceWithMock = new DatabaseServiceImpl(mockRepository);

        when(mockRepository.saveAll(any())).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(DatabaseException.class, () -> serviceWithMock.persistWithRetry(validMarketModels));

        verify(mockRepository, times(1)).saveAll(validMarketModels);
    }
}