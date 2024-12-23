package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.services.dto.MarketDTO;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataAggregateService {
    List<RawExchangesModel> fetchExchanges();
    List<RawAssetModel> fetchAssets();
    void asyncFetch();
    List<MarketDTO> fetchScheduledMarketPrice() throws DataAggregateException;
    List<MarketDTO> collectAndUpdateMarketState();
    Boolean isPriceChangeMeaningful(@Nonnull MarketDTO cachedData);
    void completeMarketAttributes() throws DataAggregateException;
}
