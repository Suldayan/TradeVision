package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.RawAssetModel;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RawAssetModelRepository extends CrudRepository<RawAssetModel, UUID> {
}
