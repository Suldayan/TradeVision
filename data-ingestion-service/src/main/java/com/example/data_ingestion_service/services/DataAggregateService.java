package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DataAggregateService {
    List<RawExchangesModel> fetchExchanges();
    List<RawAssetModel> fetchAssets();
    List<RawMarketModel> fetchMarkets();
    CompletableFuture<Void> asyncFetch();
    List<RawMarketModel> collectAndUpdateMarketState();
    Set<String> filterExchangeIds();
    Set<String> filterAssetIds();
    Set<RawExchangesModel> exchangeIdsToModels();
    Set<RawAssetModel> assetIdsToModels();
    Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData);
}
