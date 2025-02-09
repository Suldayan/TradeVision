package com.example.data_processing_service.repository;

import com.example.data_processing_service.models.MarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MarketModelRepository extends JpaRepository<MarketModel, UUID> {
    Set<MarketModel> findAllByTimeRange(
            ZonedDateTime start,
            ZonedDateTime end);

    Set<MarketModel> findByBaseIdAndTimeRange(
            ZonedDateTime start,
            ZonedDateTime end,
            String id
    );

    Set<MarketModel> findByQuoteIdAndTimeRange(
            ZonedDateTime start,
            ZonedDateTime end,
            String id
    );

    Set<MarketModel> findByExchangeIdAndTimeRange(
            ZonedDateTime start,
            ZonedDateTime end,
            String id
    );
}
