package com.example.data_processing_service.database.service;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.service.exception.DatabaseException;

import java.util.Set;

public interface DatabaseService {
    void saveToDatabase(Set<MarketModel> marketModels) throws DatabaseException, IllegalArgumentException;
    void persistWithRetry(Set<MarketModel> marketModels) throws DatabaseException;
}