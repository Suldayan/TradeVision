package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import org.springframework.data.repository.CrudRepository;

public interface RawExchangeModelRepository extends CrudRepository<RawExchangesModel, String> {
}
