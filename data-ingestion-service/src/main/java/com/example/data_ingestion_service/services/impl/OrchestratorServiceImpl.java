package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorServiceImpl implements OrchestratorService {
    private final MarketService marketService;
    private final DatabaseService databaseService;

    @Override
    public void executeDataPipeline() {
        try {
            log.info("Pipeline execution has started");
            Set<RawMarketModel> marketModels = marketService.convertToModel();
            databaseService.saveToDatabase(marketModels);
            log.info("Pipeline execution has completed successfully");
        } catch (Exception ex) {
            log.error("Error during the pipeline execution: {}", ex.getMessage(), ex);
        }
    }
}
