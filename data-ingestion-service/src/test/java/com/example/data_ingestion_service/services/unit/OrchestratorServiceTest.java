package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.OrchestratorService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import com.example.data_ingestion_service.services.exceptions.OrchestratorException;
import com.example.data_ingestion_service.services.impl.OrchestratorServiceImpl;
import com.example.data_ingestion_service.services.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.KafkaException;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = OrchestratorServiceImpl.class,
        properties = {
                "resilience4j.circuitbreaker.instances.marketService.slidingWindowSize=5",
                "resilience4j.circuitbreaker.instances.marketService.failureRateThreshold=50",
                "resilience4j.circuitbreaker.instances.kafkaProducer.slidingWindowSize=5",
                "resilience4j.circuitbreaker.instances.kafkaProducer.failureRateThreshold=50",
                "spring.retry.max-attempts=3"
        }
)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Execution(ExecutionMode.SAME_THREAD)
class OrchestratorServiceTest {

    @Autowired
    OrchestratorService orchestratorService;

    @MockBean
    MarketService marketService;

    @MockBean
    DatabaseService databaseService;

    @MockBean
    KafkaProducer kafkaProducer;

    private final RawMarketModel sampleModel = new RawMarketModel();

    @BeforeEach
    void setup() {
        sampleModel.setTimestamp(12345L);

        // Build valid Market with required parameters
        Market validMarket = Market.builder()
                .exchangeId("binance")
                .rank(1)
                .baseSymbol("BTC")
                .baseId("bitcoin")
                .quoteSymbol("USDT")
                .quoteId("tether")
                .priceQuote(BigDecimal.ONE)
                .priceUsd(BigDecimal.TEN)
                .volumeUsd24Hr(new BigDecimal("50000.00"))
                .percentExchangeVolume(new BigDecimal("0.5"))
                .tradesCount24Hr(1500)
                .updated(123456789L)
                .timestamp(null)  // nullable field
                .build();

        MarketWrapper validWrapper = new MarketWrapper(Set.of(validMarket), 1242335L);

        when(marketService.getMarketsData()).thenReturn(validWrapper);
        when(marketService.convertWrapperDataToRecord(any()))
                .thenReturn(Set.of(validMarket));
        when(marketService.convertToModel(any()))
                .thenReturn(Set.of(sampleModel));
    }

    @Test
    void executeDataPipeline_SuccessfulExecution() throws DatabaseException {
        assertDoesNotThrow(() -> orchestratorService.executeDataPipeline());

        verify(databaseService).saveToDatabase(anySet());
        verify(kafkaProducer).sendMessage(argThat(event ->
                event.getStatus().equals("Completed successfully") &&
                        event.getTimestamp().equals(12345L)
        ));
    }

    @Test
    void executeDataPipeline_ThrowsOrchestratorExceptionOnApiFailure() {
        when(marketService.getMarketsData()).thenThrow(new ApiException("Market API down"));

        OrchestratorException ex = assertThrows(OrchestratorException.class,
                () -> orchestratorService.executeDataPipeline());

        assertTrue(ex.getMessage().contains("API error"));
        assertInstanceOf(ApiException.class, ex.getCause());
    }

    @Test
    void fetchAndConvertData_RetriesOnApiException() {
        Market validMarket = Market.builder()
                .exchangeId("binance")
                .rank(1)
                .baseSymbol("BTC")
                .baseId("bitcoin")
                .quoteSymbol("USDT")
                .quoteId("tether")
                .priceQuote(BigDecimal.ONE)
                .priceUsd(BigDecimal.TEN)
                .volumeUsd24Hr(new BigDecimal("50000.00"))
                .percentExchangeVolume(new BigDecimal("0.5"))
                .tradesCount24Hr(1500)
                .updated(123456789L)
                .timestamp(null)
                .build();

        RawMarketModel validRawMarketModel = RawMarketModel.builder()
                .exchangeId("binance")
                .rank(1)
                .baseSymbol("BTC")
                .baseId("bitcoin")
                .quoteSymbol("USDT")
                .quoteId("tether")
                .priceQuote(BigDecimal.ONE)
                .priceUsd(BigDecimal.TEN)
                .volumeUsd24Hr(new BigDecimal("50000.00"))
                .percentExchangeVolume(new BigDecimal("0.5"))
                .tradesCount24Hr(1500)
                .updated(123456789L)
                .timestamp(null)
                .build();
        /*
        when(marketService.getMarketsData())
                .thenThrow(new ApiException("Temporary error"))
                .thenReturn(new MarketWrapper(Set.of(validMarket), 123234L));
        */
        when(marketService.convertToModel(Set.of(validMarket)))
                .thenThrow(new ApiException("Temporary error"))
                .thenReturn(Set.of(validRawMarketModel));

        Set<RawMarketModel> result = assertDoesNotThrow(() ->
                orchestratorService.fetchAndConvertData()
        );

        System.out.println(result.size());
        assertFalse(result.isEmpty());
        verify(marketService, times(3)).getMarketsData();
    }

    @Test
    void notifyPipelineCompletion_CircuitBreakerOpensAfterMultipleFailures() {
        doThrow(new KafkaException("Broker unavailable"))
                .when(kafkaProducer).sendMessage(any());

        IntStream.range(0, 5).forEach(i ->
                assertThrows(KafkaException.class,
                        () -> orchestratorService.notifyPipelineCompletion(12345L))
        );

        assertDoesNotThrow(() -> orchestratorService.notifyPipelineCompletion(12345L));

        verify(kafkaProducer, times(5)).sendMessage(any());
    }

    @Test
    void saveData_RetriesOnDatabaseFailure() throws DatabaseException {
        doThrow(new DataAccessException("Connection timeout") {})
                .doNothing()
                .when(databaseService).saveToDatabase(anySet());

        assertDoesNotThrow(() ->
                orchestratorService.saveData(Set.of(sampleModel))
        );

        verify(databaseService, times(2)).saveToDatabase(anySet());
    }

    @Test
    void handleMarketFailure_FallbackReturnsEmptySet() {
        when(marketService.getMarketsData()).thenThrow(new RuntimeException("Critical failure"));

        Set<RawMarketModel> result = orchestratorService.fetchAndConvertData();

        assertTrue(result.isEmpty());
        verify(marketService, atLeastOnce()).getMarketsData();
    }
}
