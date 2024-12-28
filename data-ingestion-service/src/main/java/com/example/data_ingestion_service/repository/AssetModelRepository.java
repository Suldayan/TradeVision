package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.processed.AssetModel;
import org.springframework.data.repository.CrudRepository;

public interface AssetModelRepository extends CrudRepository<AssetModel, String> {
}
