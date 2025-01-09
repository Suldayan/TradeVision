package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.records.Asset;

import java.util.Set;

public interface AssetService {
    Set<Asset> getAssetData();
    Set<RawAssetModel> convertToModel();
}
