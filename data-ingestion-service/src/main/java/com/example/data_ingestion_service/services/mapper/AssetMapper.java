package com.example.data_ingestion_service.services.mapper;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.records.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);

    Set<RawAssetModel> assetRecordToEntity(Set<Asset> assetRecords);
}
