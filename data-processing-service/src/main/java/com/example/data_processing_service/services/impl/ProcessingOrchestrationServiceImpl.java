package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.processed.MarketModel;
import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.DataPersistenceService;
import com.example.data_processing_service.services.ProcessingOrchestratorService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessingOrchestrationServiceImpl implements ProcessingOrchestratorService {
    private final DataNormalizationService dataNormalizationService;
    private final DataPersistenceService dataPersistenceService;

    @Override
    public void startProcessingFlow(@Nonnull Long timestamp) {
        Set<MarketModel> marketModels = dataNormalizationService.removeFields(timestamp);
        dataPersistenceService.saveToDatabase(marketModels);
    }
}
