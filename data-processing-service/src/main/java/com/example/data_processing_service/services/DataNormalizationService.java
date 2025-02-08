package com.example.data_processing_service.services;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;

import java.util.Set;

public interface DataNormalizationService {
    Set<MarketModel> transformToMarketModel(Set<RawMarketModel> rawMarketModels ,Long timestamp) throws IllegalArgumentException;
}
