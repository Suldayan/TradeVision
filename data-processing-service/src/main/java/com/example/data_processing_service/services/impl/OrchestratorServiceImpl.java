package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.dto.RawMarketDTO;
import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.IngestionService;
import com.example.data_processing_service.services.OrchestratorService;
import com.example.data_processing_service.services.exception.DatabaseException;
import com.example.data_processing_service.services.exception.IngestionException;
import com.example.data_processing_service.services.exception.OrchestratorException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorServiceImpl implements OrchestratorService {
    private static final String PROCESSING_CONTEXT = "timestamp=%s";

    private final DataNormalizationService dataNormalizationService;
    private final DataPersistenceService dataPersistenceService;
    private final IngestionService ingestionService;

    @Override
    public void startProcessingFlow(@Nonnull Long timestamp) throws OrchestratorException {
        final String context = String.format(PROCESSING_CONTEXT, timestamp);
        log.info("Starting processing flow for {} at {}", context, LocalDateTime.now());
        try {
            Set<RawMarketDTO> rawMarketModels = ingestionService.fetchRawMarkets(timestamp);
            log.debug("Successfully fetched {} raw market records for {}", rawMarketModels.size(), context);
            Set<MarketModel> marketModels = dataNormalizationService.transformToMarketModel(rawMarketModels, timestamp);
            log.debug("Transformed {} raw records to normalized models for {}", marketModels.size(), context);
            dataPersistenceService.saveToDatabase(marketModels);
            log.info("Successfully completed processing flow for {} at {}. Persisted {} records.",
                    context, LocalDateTime.now(), marketModels.size());

        } catch (IngestionException ex) {
            throw new OrchestratorException(String.format("Data ingestion failed for %s", context), ex);
        } catch (IllegalArgumentException ex) {
            throw new OrchestratorException(String.format("Data validation failed for %s", context), ex);
        } catch (DatabaseException ex) {
            throw new OrchestratorException(String.format("Database operation failed for %s", context), ex);
        } catch (Exception ex) {
            throw new OrchestratorException(String.format("Unexpected error in processing flow for %s", context), ex);
        }
    }
}