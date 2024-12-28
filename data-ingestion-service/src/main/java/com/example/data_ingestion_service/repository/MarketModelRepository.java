package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.processed.MarketModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketModelRepository extends CrudRepository<MarketModel, String> {

}
