package com.example.data_ingestion_service.records;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record Asset(
        @Nonnull String id,
        @Nonnull Integer rank,
        @Nonnull String symbol,
        @Nonnull String name,
        @Nonnull BigDecimal supply,
        @Nonnull BigDecimal maxSupply,
        @Nonnull BigDecimal marketCapUsd,
        @Nonnull BigDecimal volume24Hr,
        @Nonnull BigDecimal priceUsd,
        @Nonnull Double changePercent24Hr,
        @Nonnull BigDecimal vwap24Hr,
        @Nonnull String explorer) {
}
