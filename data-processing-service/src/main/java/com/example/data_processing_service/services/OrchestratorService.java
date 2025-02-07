package com.example.data_processing_service.services;

import com.example.data_processing_service.services.exception.OrchestratorException;

public interface OrchestratorService {
    void startProcessingFlow(Long timestamp) throws OrchestratorException;
}
