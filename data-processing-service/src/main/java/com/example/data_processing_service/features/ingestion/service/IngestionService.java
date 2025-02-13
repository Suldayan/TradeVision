package com.example.data_processing_service.features.ingestion.service;

import com.example.data_processing_service.features.ingestion.exception.IngestionException;
import com.example.data_processing_service.features.shared.dto.RawMarketDTO;

import java.util.Set;

public interface IngestionService {
    Set<RawMarketDTO> fetchRawMarkets(Long timestamp) throws IllegalArgumentException, IngestionException;
}
