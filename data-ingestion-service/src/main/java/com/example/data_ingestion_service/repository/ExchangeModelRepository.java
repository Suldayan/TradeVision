package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.processed.ExchangeModel;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeModelRepository extends CrudRepository<ExchangeModel, String> {
}
