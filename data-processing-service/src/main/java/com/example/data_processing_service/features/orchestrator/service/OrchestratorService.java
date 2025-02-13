package com.example.data_processing_service.features.orchestrator.service;

import com.example.data_processing_service.features.orchestrator.exception.OrchestratorException;

public interface OrchestratorService {
    void startProcessingFlow(Long timestamp) throws OrchestratorException;
}