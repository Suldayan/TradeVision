package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.services.exceptions.OrchestratorException;

public interface OrchestratorService {
    void executeDataPipeline() throws OrchestratorException;
}
