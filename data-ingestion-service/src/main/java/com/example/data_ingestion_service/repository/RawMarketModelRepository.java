package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.RawMarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RawMarketModelRepository extends CrudRepository<RawMarketModel, String> {

}
