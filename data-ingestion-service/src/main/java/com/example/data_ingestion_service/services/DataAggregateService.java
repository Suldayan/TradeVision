package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import jakarta.annotation.Nonnull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DataAggregateService {
    List<RawExchangesModel> fetchExchanges();
    List<RawAssetModel> fetchAssets();
    List<RawMarketModel> fetchMarkets();
    CompletableFuture<Void> asyncFetch();
}
