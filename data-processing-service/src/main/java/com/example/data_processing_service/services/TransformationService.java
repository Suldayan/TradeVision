package com.example.data_processing_service.services;

import com.example.data_processing_service.models.processed.AssetModel;
import com.example.data_processing_service.models.processed.ExchangesModel;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.Set;

public interface TransformationService {
    Set<ExchangesModel> rawToEntityExchange(Set<String> ids);
    Set<AssetModel> rawToEntityAsset(Set<String> ids);
    Map<String, ExchangesModel> indexExchangesById(Set<ExchangesModel> exchangesModels);
    Map<String, AssetModel> indexAssetsById(Set<AssetModel> assetModels);
    void completeMarketAttributes();
    <S> void saveToDatabase(@Nonnull Set<S> entity);
}
