package com.example.data_processing_service.features.orchestrator.service.Impl;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.service.DatabaseService;
import com.example.data_processing_service.database.service.exception.DatabaseException;
import com.example.data_processing_service.features.ingestion.exception.IngestionException;
import com.example.data_processing_service.features.ingestion.service.IngestionService;
import com.example.data_processing_service.features.orchestrator.exception.OrchestratorException;
import com.example.data_processing_service.features.orchestrator.service.OrchestratorService;
import com.example.data_processing_service.features.processing.service.DataNormalizationService;
import com.example.data_processing_service.features.shared.dto.RawMarketDTO;
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
    private final DatabaseService databaseService;
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
            databaseService.saveToDatabase(marketModels);
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