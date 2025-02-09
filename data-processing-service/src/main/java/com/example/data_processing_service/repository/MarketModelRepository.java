package com.example.data_processing_service.repository;

import com.example.data_processing_service.models.MarketModel;
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
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp,
            String baseId
    );

    Set<MarketModel> findByQuoteIdAndTimestampBetween(
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp,
            String quoteId
    );

    Set<MarketModel> findByExchangeIdAndTimestampBetween(
            ZonedDateTime startTimestamp,
            ZonedDateTime endTimestamp,
            String exchangeId
    );
}
