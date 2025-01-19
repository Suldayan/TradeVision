package com.example.data_processing_service.models.mapper;

import com.example.data_processing_service.models.processed.AssetModel;
import com.example.data_processing_service.models.raw.RawAssetModel;
import jakarta.annotation.Nonnull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AssetMapper {
    AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);

    AssetModel rawToProcessedAsset(@Nonnull RawAssetModel rawAssetModel);
}
