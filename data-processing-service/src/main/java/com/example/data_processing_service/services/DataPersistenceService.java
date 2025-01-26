package com.example.data_processing_service.services;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.services.exception.DataNotFoundException;

import java.util.Set;

public interface DataPersistenceService {
    void saveToDatabase(Set<MarketModel> marketModels) throws DataNotFoundException;
}
