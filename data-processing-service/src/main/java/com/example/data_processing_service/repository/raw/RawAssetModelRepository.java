package com.example.data_processing_service.repository.raw;

import com.example.data_processing_service.models.raw.RawAssetModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface RawAssetModelRepository extends CrudRepository<RawAssetModel, String> {
    Set<RawAssetModel> findAllByTimestamp(Long timestamp);
}
