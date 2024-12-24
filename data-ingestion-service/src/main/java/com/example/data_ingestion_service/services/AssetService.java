package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.models.RawAssetWrapperModel;

import java.util.List;

public interface AssetService {
    List<RawAssetModel> getAssetData();
}
