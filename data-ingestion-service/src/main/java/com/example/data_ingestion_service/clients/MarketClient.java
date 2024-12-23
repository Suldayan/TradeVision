package com.example.data_ingestion_service.clients;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.models.RawMarketWrapperModel;
import jakarta.annotation.Nonnull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface MarketClient {
    @GetExchange(url = "/markets")
    RawMarketWrapperModel getMarkets();
}
