package com.example.data_processing_service.services;

import com.example.data_processing_service.models.raw.RawAssetModel;
import com.example.data_processing_service.models.raw.RawExchangesModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import jakarta.annotation.Nonnull;

import java.util.Set;

public interface FilterService {
    Set<RawMarketModel> filterMarkets();
    Set<String> filterExchangeIds(Set<RawMarketModel> filteredMarketModels);
    Set<String> filterAssetIds(Set<RawMarketModel> filteredMarketModels);
    Set<RawExchangesModel> exchangeIdsToModels(Set<String> exchangeIds);
    Set<RawAssetModel> assetIdsToModels(Set<String> assetIds);
    Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData);
}
