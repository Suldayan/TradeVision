package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawAssetWrapperModel;
import com.example.data_ingestion_service.services.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetClient assetClient;

    @Override
    public RawAssetWrapperModel getAssetData() {
        return assetClient.getAssets();
    }

    @Override
    public List<RawAssetModel> getAssetDataAsList() {
        return getAssetData().getRawAssetModelList();
    }
}
