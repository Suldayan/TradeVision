package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.processed.AssetModel;
import com.example.data_ingestion_service.models.processed.ExchangeModel;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.Set;

public interface DataTransformationService {
    Set<ExchangeModel> rawToEntityExchange();
    Set<AssetModel> rawToEntityAsset();
    Map<String, ExchangeModel> indexExchanges();
    Map<String, AssetModel> indexAssets();
    void completeMarketAttributes();
    <T> void saveToDatabase(@Nonnull T entity);
}
