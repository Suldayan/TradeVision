package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawMarketModel;
import jakarta.annotation.Nonnull;

import java.util.Set;

public interface DatabaseService {
    boolean saveToDatabase(@Nonnull Set<RawMarketModel> marketModels);
}
