package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.OrchestratorService;
import com.example.data_ingestion_service.services.dto.EventDTO;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import com.example.data_ingestion_service.services.exceptions.OrchestratorException;
import com.example.data_ingestion_service.services.exceptions.ValidationException;
import com.example.data_ingestion_service.services.producer.KafkaProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.KafkaException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorServiceImpl implements OrchestratorService {
    private final MarketService marketService;
    private final DatabaseService databaseService;
    private final KafkaProducer kafkaProducer;

    private static final String RESILIENCE_MARKET_INSTANCE = "marketService";
    private static final String RESILIENCE_PRODUCER_INSTANCE = "kafkaProducer";

    private static final String MARKET_FALLBACK = "handleMarketFailure";
    private static final String PRODUCER_FALLBACK = "handleProducerFailure";

    @Override
    public void executeDataPipeline() throws OrchestratorException {
        try {
            log.info("Pipeline execution started");
            Set<RawMarketModel> models = fetchAndConvertData();
            saveData(models);
            Long timestamp = getTimeStamp(models.iterator().next());
            notifyPipelineCompletion(timestamp);
            log.info("Pipeline execution completed successfully");
        } catch (ApiException ex) {
            log.error("API failure during pipeline execution", ex);
            throw new OrchestratorException("Pipeline failed: API error", ex);
        } catch (ValidationException | DatabaseException | KafkaException ex) {
            throw new OrchestratorException("Pipeline failed: " + ex.getMessage(), ex);
        }
    }

    @CircuitBreaker(name = RESILIENCE_MARKET_INSTANCE, fallbackMethod = MARKET_FALLBACK)
    @Retryable(
            value = {ApiException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Nonnull
    private Set<RawMarketModel> fetchAndConvertData() {
        MarketWrapper wrapper = marketService.getMarketsData();
        validateMarketWrapper(wrapper);
        Set<Market> records = marketService.convertWrapperDataToRecord(wrapper);
        return marketService.convertToModel(records);
    }

    @Retryable(
            value = {DatabaseException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void saveData(@Nonnull Set<RawMarketModel> models) throws DatabaseException {
        try {
            databaseService.saveToDatabase(models);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Failed to save market data", ex);
        }
    }

    @Nonnull
    private Long getTimeStamp(@Nonnull RawMarketModel model) {
        Long timestamp = model.getTimestamp();
        if (timestamp == null) {
            throw new ValidationException("Timestamp given is null");
        }
        return timestamp;
    }

    @CircuitBreaker(name = RESILIENCE_PRODUCER_INSTANCE, fallbackMethod = PRODUCER_FALLBACK)
    private void notifyPipelineCompletion(@Nonnull Long timestamp) {
        EventDTO event = EventDTO.builder()
                .status("Completed successfully")
                .timestamp(timestamp)
                .build();
        try {
            kafkaProducer.sendMessage(event);
            log.info("Kafka message event has successfully been sent");
        } catch (KafkaException ex) {
            log.error("Failed to send pipeline completion event", ex);
            throw ex;
        }
    }

    private void validateMarketWrapper(@Nonnull MarketWrapper wrapper) {
        if (wrapper.markets().isEmpty()) {
            throw new ValidationException("Empty market data received");
        }
        if (wrapper.markets().stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("Null object found within market set");
        }
    }

    private Set<RawMarketModel> handleMarketFailure(Exception ex) {
        log.error("Market Service has tripped into OPEN STATE: {}", ex.getMessage());
        return Collections.emptySet();
    }

    private void handleProducerFailure(Long timestamp, Exception ex) {
        log.error("Kafka Producer has tripped into OPEN STATE trying to send event with api timestamp: {}. ERROR: {}",
                timestamp, ex.getMessage());
    }
}
