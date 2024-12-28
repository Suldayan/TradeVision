package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawAssetModel;

import java.util.List;

public interface AssetService {
    List<RawAssetModel> getAssetData();
}
