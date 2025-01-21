package com.example.data_processing_service.services;

import com.example.data_processing_service.models.raw.RawAssetModel;
import com.example.data_processing_service.models.raw.RawExchangesModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import jakarta.annotation.Nonnull;

import java.util.Set;

public interface FilterService {
    Set<RawMarketModel> collectAndUpdateMarketState(Long timestamp);
    Set<String> filterExchangeIds(Set<RawMarketModel> filteredMarketModels);
    Set<String> filterAssetIds(Set<RawMarketModel> filteredMarketModels);
    Set<RawExchangesModel> exchangeIdsToModels(Set<String> exchangeIds, Long timestamp);
    Set<RawAssetModel> assetIdsToModels(Set<String> assetIds, Long timestamp);
    Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData);
}
