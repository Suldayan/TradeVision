package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.records.Asset;
import com.example.data_ingestion_service.records.wrapper.AssetWrapper;
import com.example.data_ingestion_service.services.AssetService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.mapper.AssetMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetClient assetClient;
    private final AssetMapper assetMapper;

    @Nonnull
    @Override
    public Set<Asset> getAssetData() throws ApiException {
        try {
            AssetWrapper assetHolder = assetClient.getAssets();
            if (assetHolder == null) {
                throw new ApiException("Asset wrapper model fetched but returned as null");
            }
            Set<Asset> assetSet = assetHolder.assets()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(field -> Asset.builder()
                            .id(field.id())
                            .rank(field.rank())
                            .symbol(field.symbol())
                            .name(field.name())
                            .supply(field.supply())
                            .maxSupply(field.maxSupply())
                            .marketCapUsd(field.marketCapUsd())
                            .volumeUsd24Hr(field.volumeUsd24Hr())
                            .priceUsd(field.priceUsd())
                            .changePercent24Hr(field.changePercent24Hr())
                            .vwap24Hr(field.vwap24Hr())
                            .explorer(field.explorer())
                            .timestamp(assetHolder.timestamp())
                            .build())
                    .collect(Collectors.toSet());
            if (assetSet.isEmpty()) {
                log.warn("Asset set returned as empty. Endpoint might be returning incomplete data");
                throw new ApiException("Asset set fetched but is empty");
            }
            log.info("Successfully fetched {} assets.", assetSet.size());
            return assetSet;
        } catch (Exception e) {
            log.error("An error occurred while fetching assets data: {}", e.getMessage(), e.getCause());
            throw new ApiException(String.format("Failed to fetch asset wrapper data: %s", e.getMessage()));
        }
    }

    @Nonnull
    @Override
    public Set<RawAssetModel> convertToModel() {
        return assetMapper.assetRecordToEntity(getAssetData());
    }
}
