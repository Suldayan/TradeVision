package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.models.RawMarketWrapperModel;
import com.example.data_ingestion_service.services.MarketService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {
    private final MarketClient marketClient;

    @Nonnull
    @Override
    public RawMarketWrapperModel getMarketData() {
        return marketClient.getMarkets();
    }

    @Nonnull
    @Override
    public List<RawMarketModel> getMarketsAsList() {
        return getMarketData().getMarketModelList();
    }
}
