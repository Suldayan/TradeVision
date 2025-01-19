package com.example.data_processing_service.services;

import com.example.data_processing_service.models.raw.RawAssetModel;
import com.example.data_processing_service.models.raw.RawExchangesModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import jakarta.annotation.Nonnull;

import java.util.Set;

public interface FilterService {
    Set<RawMarketModel> collectAndUpdateMarketState();
    Set<String> filterExchangeIds();
    Set<String> filterAssetIds();
    Set<RawExchangesModel> exchangeIdsToModels();
    Set<RawAssetModel> assetIdsToModels();
    Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData);
}
