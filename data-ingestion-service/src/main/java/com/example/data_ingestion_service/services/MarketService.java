package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;

import java.util.Set;


public interface MarketService {
    Set<Market> getMarketsData();
    Set<RawMarketModel> convertToModel();
}
