package com.example.trade_vision_backend.ingestion.core.service;

import com.example.trade_vision_backend.ingestion.infrastructure.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.infrastructure.dto.RawMarketDTO;

import java.util.Set;

public interface IngestionService {
    MarketWrapperDTO getMarketsData();
    Set<RawMarketDTO> convertWrapperDataToRecord(MarketWrapperDTO wrapper);
    void sendEvent(Set<RawMarketDTO> marketDTOS);
    void executeIngestion();
}
