package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.IngestionService;
import com.example.data_processing_service.services.ProcessingOrchestratorService;
import com.example.data_processing_service.services.exception.DataNotFoundException;
import com.example.data_processing_service.services.exception.ProcessingException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessingOrchestrationServiceImpl implements ProcessingOrchestratorService {
    private final DataNormalizationService dataNormalizationService;
    private final DataPersistenceService dataPersistenceService;
    private final IngestionService ingestionService;

    @Override
    public void startProcessingFlow(@Nonnull Long timestamp) throws DataNotFoundException, ProcessingException {
        log.info("Processing has started at: {}", LocalDateTime.now());
        try {
            Set<RawMarketModel> rawMarketModels = ingestionService.fetchRawMarkets(timestamp);
            Set<MarketModel> marketModels = dataNormalizationService.transformToMarketModel(rawMarketModels, timestamp);
            dataPersistenceService.saveToDatabase(marketModels);
            log.info("Processing completed successfully at: {}", LocalDateTime.now());
        } catch (DataNotFoundException e) {
            log.error("Error fetching markets during the processing flow. Market data assumed to be empty: {}", e.getMessage(), e);
            throw new DataNotFoundException("Failed to fetch market data during orchestration", e);
        } catch (Exception e) {
            log.error("Error processing overall service flow with data timestamp={}. Error Message={}",
                    timestamp, e.getMessage(), e);
            throw new ProcessingException("Failed to initiate processing flow", e);
        }
    }
}
