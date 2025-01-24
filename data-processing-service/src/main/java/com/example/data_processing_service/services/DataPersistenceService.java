package com.example.data_processing_service.services;

import com.example.data_processing_service.models.MarketModel;

import java.util.Set;

public interface DataPersistenceService {
    void saveToDatabase(Set<MarketModel> marketModels);
}
