package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.model.RawMarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMarketModelRepository extends JpaRepository<RawMarketModel, Long> {
}
