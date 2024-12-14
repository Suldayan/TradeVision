package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.models.RawMarketWrapperModel;

import java.util.List;

public interface MarketService {
    RawMarketWrapperModel getMarketData();
    List<RawMarketModel> getMarketsAsList();
}
