package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawMarketModel;

import java.util.List;

public interface MarketService {
    List<RawMarketModel> getMarketsData();
}
