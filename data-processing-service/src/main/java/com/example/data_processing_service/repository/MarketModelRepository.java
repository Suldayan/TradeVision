package com.example.data_processing_service.repository;

import com.example.data_processing_service.models.MarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Set;

@Repository
public interface MarketModelRepository extends JpaRepository<MarketModel, String> {
    Set<MarketModel> findAllByTimestampBetween(
            ZonedDateTime start,
            ZonedDateTime end);
}
