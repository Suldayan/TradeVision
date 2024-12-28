package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.RawAssetWrapperModel;
import com.example.data_ingestion_service.services.AssetService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetClient assetClient;

    /*
     * Grabs all the market data from the assets endpoint
     * @returns a list of each asset, containing asset metadata
     * */
    @Nonnull
    @Override
    public List<RawAssetModel>  getAssetData() {
        try {
            RawAssetWrapperModel assetHolder = assetClient.getAssets();
            log.info("Fetched assets with result: {}", assetHolder);
            if (assetHolder == null) {
                log.error("Fetched data from asset wrapper returned as null");
                throw new ApiException("Assets data fetched but return as null");
            }
            return assetHolder.getRawAssetModelList();
        } catch (Exception e) {
            log.error("An error occurred while fetching assets data: {}", e.getMessage());
            throw new ApiException(String.format("Failed to fetch asset wrapper data: %s", e));
        }
    }
}
