package com.example.data_processing_service.services;

import com.example.data_processing_service.services.exception.DataNotFoundException;

public interface ProcessingOrchestratorService {
    void startProcessingFlow(Long timestamp) throws DataNotFoundException;
}
