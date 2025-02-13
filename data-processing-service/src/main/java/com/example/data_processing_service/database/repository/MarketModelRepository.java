package com.example.data_processing_service.database.repository;

import com.example.data_processing_service.database.model.MarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MarketModelRepository extends JpaRepository<MarketModel, UUID> {
    Set<MarketModel> findAllByTimestampBetween(
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp);

    Set<MarketModel> findByBaseIdAndTimestampBetween(
            String baseId,
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp
    );

    Set<MarketModel> findByQuoteIdAndTimestampBetween(
            String quoteId,
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp
    );

    Set<MarketModel> findByExchangeIdAndTimestampBetween(
            String exchangeId,
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp
    );
}