package com.example.data_processing_service.repository;

import com.example.data_processing_service.models.MarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MarketModelRepository extends JpaRepository<MarketModel, String> {
    List<MarketModel> findAllByTimestampBetween(ZonedDateTime start, ZonedDateTime end);
}
