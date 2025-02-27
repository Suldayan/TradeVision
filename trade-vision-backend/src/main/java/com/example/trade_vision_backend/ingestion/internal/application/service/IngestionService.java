package com.example.trade_vision_backend.ingestion.internal.application.service;

import com.example.trade_vision_backend.ingestion.internal.domain.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.internal.domain.dto.RawMarketDTO;

import java.util.Set;

public interface IngestionService {
    MarketWrapperDTO getMarketsData();
    Set<RawMarketDTO> convertWrapperDataToRecord(MarketWrapperDTO wrapper);
    void sendEvent(Set<RawMarketDTO> marketDTOS);
    void executeIngestion();
}
