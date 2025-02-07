package com.example.data_processing_service.services;

import com.example.data_processing_service.services.exception.DataValidationException;
import com.example.data_processing_service.services.exception.ProcessingException;

public interface OrchestratorService {
    void startProcessingFlow(Long timestamp) throws ProcessingException, DataValidationException;
}
