package com.example.data_ingestion_service.repository;

import com.example.data_ingestion_service.models.RawMarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface RawMarketModelRepository extends JpaRepository<RawMarketModel, UUID> {
    Set<RawMarketModel> findAllByTimestamp(Long timestamp);
}
