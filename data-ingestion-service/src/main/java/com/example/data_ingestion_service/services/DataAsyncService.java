package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawMarketModel;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DataAsyncService {
    CompletableFuture<Set<RawExchangesModel>> fetchExchanges();
    CompletableFuture<Set<RawAssetModel>> fetchAssets();
    CompletableFuture<Set<RawMarketModel>> fetchMarkets();
    CompletableFuture<Void> asyncFetch();
}
