package com.example.data_processing_service.services;

import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.exception.DataValidationException;

import java.util.Set;

public interface IngestionService {
    Set<RawMarketModel> fetchRawMarkets(Long timestamp) throws DataValidationException;
}
