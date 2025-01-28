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

    @Nonnull
    private Set<RawMarketModel> fetchAndConvertData() {
        MarketWrapper wrapper = marketService.getMarketsData();
        validateMarketWrapper(wrapper);
        Set<Market> records = marketService.convertWrapperDataToRecord(wrapper);
        return marketService.convertToModel(records);
    }

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

    private void notifyPipelineCompletion(@Nonnull Long timestamp) {
        EventDTO event = EventDTO.builder()
                .status("Completed successfully")
                .timestamp(timestamp)
                .build();
        try {
            kafkaProducer.sendMessage(event);
        } catch (KafkaException ex) {
            log.error("Failed to send pipeline completion event", ex);
            throw ex;
        }
    }

    private void validateMarketWrapper(@Nonnull MarketWrapper wrapper) {
        if (wrapper.markets().isEmpty()) {
            throw new ValidationException("Empty market data received");
        }
    }
}
