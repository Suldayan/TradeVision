package com.example.trade_vision_backend.ingestion.market.application.service;

import com.example.trade_vision_backend.ingestion.market.domain.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import com.example.trade_vision_backend.ingestion.market.infrastructure.model.RawMarketModel;

import java.util.Set;

public interface MarketService {
    MarketWrapperDTO getMarketsData();
    Set<RawMarketDTO> convertWrapperDataToRecord(MarketWrapperDTO wrapper);
    Set<RawMarketModel> rawMarketDTOToModel(Set<RawMarketDTO> marketDTOS);
}
