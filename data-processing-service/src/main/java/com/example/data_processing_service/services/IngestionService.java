package com.example.data_processing_service.services;

import com.example.data_processing_service.models.RawMarketModel;

import java.util.Set;

public interface IngestionService {
    Set<RawMarketModel> fetchRawMarkets(Long timestamp) throws IllegalArgumentException;
}
