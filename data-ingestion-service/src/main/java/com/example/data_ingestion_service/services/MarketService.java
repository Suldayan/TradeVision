package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import jakarta.annotation.Nonnull;
import org.yaml.snakeyaml.error.Mark;

import java.util.Set;


public interface MarketService {
    MarketWrapper getMarketsData();
    Set<Market> convertWrapperDataToRecord();
    Set<RawMarketModel> convertToModel();
}
