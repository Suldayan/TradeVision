package com.example.trade_vision_backend.ingestion.internal.domain.client;

import com.example.trade_vision_backend.ingestion.internal.domain.dto.MarketWrapperDTO;
import org.springframework.web.service.annotation.GetExchange;

public interface IngestionClient {
    @GetExchange(url = "/markets")
    MarketWrapperDTO getMarkets();
}