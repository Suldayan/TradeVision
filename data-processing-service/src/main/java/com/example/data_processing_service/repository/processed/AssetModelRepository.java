package com.example.data_processing_service.repository.processed;

import com.example.data_processing_service.models.processed.AssetModel;
import org.springframework.data.repository.CrudRepository;

public interface AssetModelRepository extends CrudRepository<AssetModel, String> {
}
