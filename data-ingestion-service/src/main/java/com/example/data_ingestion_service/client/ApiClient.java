package com.example.data_ingestion_service.client;

import com.example.data_ingestion_service.model.RawAssetHistoryWrapperModel;
import com.example.data_ingestion_service.model.RawAssetWrapperModel;
import com.example.data_ingestion_service.model.RawExchangeWrapperModel;
import com.example.data_ingestion_service.model.RawMarketWrapperModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ApiClient {

    /**
     * Retrieves market data for all available assets.
     * @return ResponseEntity containing RawMarketWrapperModel
     */
    @GetExchange("/market")
    ResponseEntity<RawMarketWrapperModel> getAllMarketData();

    /**
     * Retrieves specific historical asset data by asset ID.
     * @param id The unique identifier of the asset.
     * @return ResponseEntity containing RawAssetWrapperModel
     */
    @GetExchange("/asset/{id}/history")
    ResponseEntity<RawAssetWrapperModel> getAssetSpecificHistoricData(@PathVariable String id);

    /**
     * Retrieves data for all assets.
     * @return ResponseEntity containing RawAssetHistoryWrapperModel
     */
    @GetExchange("/asset")
    ResponseEntity<RawAssetHistoryWrapperModel> getAllAssetData();

    /**
     * Retrieves data for all available exchanges.
     * @return ResponseEntity containing RawExchangeWrapperModel
     */
    @GetExchange("/exchange")
    ResponseEntity<RawExchangeWrapperModel> getAllExchangeData();
}
