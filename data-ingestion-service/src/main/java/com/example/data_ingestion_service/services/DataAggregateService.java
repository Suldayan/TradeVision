package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import jakarta.annotation.Nonnull;

import java.util.List;

public interface DataAggregateService {
    List<RawExchangesModel> fetchExchanges();
    List<RawAssetModel> fetchAssets();
    List<RawMarketModel> fetchMarkets();
    void asyncFetch();
    List<RawMarketModel> collectAndUpdateMarketState();
    Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData);
}
