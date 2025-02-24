package com.example.trade_vision_backend.ingestion.domain.service;

import com.example.trade_vision_backend.ingestion.domain.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.domain.RawMarketDTO;

import java.util.Set;

public interface IngestionService {
    MarketWrapperDTO getMarketsData();
    Set<RawMarketDTO> convertWrapperDataToRecord(MarketWrapperDTO wrapper);
    void sendEvent(Set<RawMarketDTO> marketDTOS);
    void executeIngestion();
}
