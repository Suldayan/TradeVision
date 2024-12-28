package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.raw.RawMarketModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMarketModelRepository extends CrudRepository<RawMarketModel, String> {

}
