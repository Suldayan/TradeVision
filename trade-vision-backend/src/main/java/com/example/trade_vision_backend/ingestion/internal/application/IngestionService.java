package com.example.trade_vision_backend.ingestion.internal.application;

import com.example.trade_vision_backend.ingestion.market.RawMarketDTO;

import java.util.Set;

public interface IngestionService {
    void sendEvent(Set<RawMarketDTO> marketDTOS);
    void executeIngestion();
}
