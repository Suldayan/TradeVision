package com.example.data_processing_service.services;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.services.exception.DataNotFoundException;
import com.example.data_processing_service.services.exception.DataValidationException;
import com.example.data_processing_service.services.exception.DatabaseException;

import java.util.Set;

public interface DataPersistenceService {
    void saveToDatabase(Set<MarketModel> marketModels) throws DatabaseException, DataValidationException;
    void persistWithRetry(Set<MarketModel> marketModels) throws DatabaseException;
}
