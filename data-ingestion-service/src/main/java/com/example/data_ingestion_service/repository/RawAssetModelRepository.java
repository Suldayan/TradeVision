package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.RawAssetModel;
import org.springframework.data.repository.CrudRepository;

public interface RawAssetModelRepository extends CrudRepository<RawAssetModel, String> {
}
