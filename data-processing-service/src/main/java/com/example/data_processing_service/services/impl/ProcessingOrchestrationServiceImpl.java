package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.ProcessingOrchestratorService;
import com.example.data_processing_service.services.exception.DataNotFoundException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessingOrchestrationServiceImpl implements ProcessingOrchestratorService {
    private final DataNormalizationService dataNormalizationService;
    private final DataPersistenceService dataPersistenceService;

    @Override
    public void startProcessingFlow(@Nonnull Long timestamp) throws DataNotFoundException {
        log.info("Processing has started at: {}", LocalTime.now());
        try {
            Set<MarketModel> marketModels = dataNormalizationService.transformToMarketModel(timestamp);
            dataPersistenceService.saveToDatabase(marketModels);
            log.info("Processing completed successfully at: {}", LocalTime.now());
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(String.format("Failed to fetch markets during the processing flow. Market data assumed to be empty: %s",
                    e.getMessage()), e);
        } catch (Exception e) {
            log.error("Failed to process overall service flow with timestamped data at: {}",
                    timestamp, e);
            throw e;
        }
    }
}
