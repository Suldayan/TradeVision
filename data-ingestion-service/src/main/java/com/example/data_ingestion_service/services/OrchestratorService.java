package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import com.example.data_ingestion_service.services.exceptions.OrchestratorException;
import jakarta.annotation.Nonnull;

import java.util.Set;

public interface OrchestratorService {
    void executeDataPipeline() throws OrchestratorException;
    Set<RawMarketModel> fetchAndConvertData();
    void notifyPipelineCompletion(Long timestamp);
    void saveData(@Nonnull Set<RawMarketModel> models) throws DatabaseException;
}
