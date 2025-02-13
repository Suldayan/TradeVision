package com.example.data_processing_service.features.processing.service;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.features.shared.dto.RawMarketDTO;

import java.util.Set;

public interface DataNormalizationService {
    Set<MarketModel> transformToMarketModel(Set<RawMarketDTO> rawMarketModels , Long timestamp) throws IllegalArgumentException;
}
