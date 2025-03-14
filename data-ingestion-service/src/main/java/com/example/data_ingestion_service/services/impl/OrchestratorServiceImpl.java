package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
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
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorServiceImpl implements OrchestratorService {
    private final MarketService marketService;
    private final DatabaseService databaseService;
    private final KafkaProducer kafkaProducer;

    private static final String RESILIENCE_PIPELINE_INSTANCE = "pipeline";
    private static final String PIPELINE_FALLBACK = "handlePipelineFailure";

    @CircuitBreaker(name = RESILIENCE_PIPELINE_INSTANCE, fallbackMethod = PIPELINE_FALLBACK)
    @Override
    public void executeDataPipeline() throws OrchestratorException {
        log.info("Pipeline execution started");
        try {
            log.info("PIPELINE: Fetching markets...");
            Set<RawMarketModel> models = marketService.convertToModel();
            log.info("PIPELINE: Saving markets...");
            saveData(models);
            Long timestamp = getTimeStamp(models.iterator().next());
            log.info("PIPELINE: Notifying completion...");
            notifyPipelineCompletion(timestamp);
            log.info("Pipeline execution completed successfully!");
        } catch (ApiException ex) {
            log.error("API failure during pipeline execution", ex);
            throw new OrchestratorException("Pipeline failed: API error", ex);
        } catch (ValidationException ex) {
            log.error("Validation failed: {}", ex.getMessage());
            throw new OrchestratorException("Validation error in pipeline: " + ex.getMessage(), ex);
        } catch (DatabaseException ex) {
            log.error("Database operation failed: {}", ex.getMessage());
            throw new OrchestratorException("Database error in pipeline: " + ex.getMessage(), ex);
        } catch (KafkaException ex) {
            log.error("Kafka operation failed: {}", ex.getMessage());
            throw new OrchestratorException("Kafka error in pipeline: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void saveData(@Nonnull Set<RawMarketModel> models) throws DatabaseException {
        try {
            databaseService.saveToDatabase(models);
        } catch (DataAccessException ex) {
            log.error("DataAccessException occurred, retrying...", ex);
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

    @Override
    public void notifyPipelineCompletion(@Nonnull Long timestamp) {
        EventDTO event = EventDTO.builder()
                .status("Completed successfully")
                .timestamp(timestamp)
                .build();
        try {
            kafkaProducer.sendMessage(event);
            log.info("Kafka message has successfully been sent");
        } catch (KafkaException ex) {
            log.error("Failed to send pipeline completion event", ex);
            throw ex;
        }
    }

    private void handlePipelineFailure(Exception ex) {
        log.error("Data ingestion pipeline has tripped into OPEN STATE: {}", ex.getMessage());
    }
}
