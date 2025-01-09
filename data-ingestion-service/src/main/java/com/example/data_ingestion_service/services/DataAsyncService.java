package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.models.RawMarketModel;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DataAsyncService {
    Set<RawExchangesModel> fetchExchanges();
    Set<RawAssetModel> fetchAssets();
    Set<RawMarketModel> fetchMarkets();
    CompletableFuture<Void> asyncFetch();
}
