package com.example.data_processing_service.services;

import com.example.data_processing_service.services.exception.DataValidationException;

public interface OrchestratorService {
    void startProcessingFlow(Long timestamp) throws ProcessingException, DataValidationException;
}
