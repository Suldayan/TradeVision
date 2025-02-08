package com.example.data_processing_service.services;

import com.example.data_processing_service.dto.RawMarketDTO;

import java.util.Set;

public interface IngestionService {
    Set<RawMarketDTO> fetchRawMarkets(Long timestamp) throws IllegalArgumentException;
}
