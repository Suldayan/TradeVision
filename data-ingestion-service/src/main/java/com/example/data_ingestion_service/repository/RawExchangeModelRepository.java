package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.RawExchangesModel;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RawExchangeModelRepository extends CrudRepository<RawExchangesModel, UUID> {
}
