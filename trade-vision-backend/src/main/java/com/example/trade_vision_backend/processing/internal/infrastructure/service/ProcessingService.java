package com.example.trade_vision_backend.processing.internal.infrastructure.service;

import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import com.example.trade_vision_backend.processing.ProcessedMarketModel;
import com.example.trade_vision_backend.processing.internal.infrastructure.exception.ProcessingException;

import java.util.Set;

public interface ProcessingService {
    Set<ProcessedMarketModel> transformToMarketModel(Set<RawMarketModel> rawMarketModels, Long timestamp) throws IllegalArgumentException;
    void executeProcessing(Set<RawMarketModel> rawMarketModels, Long timestamp) throws ProcessingException;
    void saveProcessedData(Set<ProcessedMarketModel> processedData) throws ProcessingException;
}
