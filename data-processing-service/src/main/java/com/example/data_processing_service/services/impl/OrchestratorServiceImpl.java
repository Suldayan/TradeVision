package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.IngestionService;
import com.example.data_processing_service.services.OrchestratorService;
import com.example.data_processing_service.services.exception.DataValidationException;
import com.example.data_processing_service.services.exception.DatabaseException;
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
    private final DataNormalizationService dataNormalizationService;
    private final DataPersistenceService dataPersistenceService;
    private final IngestionService ingestionService;

    @Override
    public void startProcessingFlow(@Nonnull Long timestamp) throws ProcessingException, DataValidationException {
        log.info("Processing has started at: {}", LocalDateTime.now());
        try {
            Set<RawMarketModel> rawMarketModels = ingestionService.fetchRawMarkets(timestamp);
            Set<MarketModel> marketModels = dataNormalizationService.transformToMarketModel(rawMarketModels, timestamp);
            dataPersistenceService.saveToDatabase(marketModels);
            log.info("Processing completed successfully at: {}", LocalDateTime.now());
        } catch (DataValidationException e) {
            log.error("Error fetching markets during the processing flow. Market data assumed to be empty: {}", e.getMessage(), e);
            throw new DataValidationException("Failed to fetch market data during orchestration", e);
        } catch (DatabaseException ex) {
            log.error("Error saving markets within the processing flow");
        }catch (Exception e) {
            log.error("Error processing overall service flow with data timestamp={}. Error Message={}",
                    timestamp, e.getMessage(), e);
            throw new ("Failed to initiate processing flow", e);
        }
    }
}
