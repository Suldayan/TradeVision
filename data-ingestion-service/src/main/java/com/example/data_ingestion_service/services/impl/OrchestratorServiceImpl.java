package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.OrchestratorService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.OrchestratorException;
import com.example.data_ingestion_service.services.exceptions.ValidationException;
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
    public void executeDataPipeline() throws OrchestratorException {
        try {
            log.info("Pipeline execution has started");
            MarketWrapper marketWrapper = marketService.getMarketsData();
            Set<Market> marketRecords = marketService.convertWrapperDataToRecord(marketWrapper);
            Set<RawMarketModel> marketModels = marketService.convertToModel(marketRecords);
            databaseService.saveToDatabase(marketModels);
            log.info("Pipeline execution has completed successfully");
        } catch (ApiException ex) {
            log.error("API call failed: {}", ex.getMessage());
            throw new OrchestratorException("Pipeline failed due to api issue", ex);
        } catch (ValidationException ex) {
            log.error("Market validation failed: {}", ex.getMessage());
            throw new OrchestratorException("Pipeline failed due to validation issue", ex);
        }
    }
}
