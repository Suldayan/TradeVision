package com.example.data_ingestion_service.records;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Asset(
        @Nonnull String id,
        @Nonnull Integer rank,
        @Nonnull String symbol,
        @Nonnull String name,
        @Nonnull BigDecimal supply,
        @Nullable BigDecimal maxSupply,
        @Nonnull BigDecimal marketCapUsd,
        @Nonnull BigDecimal volumeUsd24Hr,
        @Nonnull BigDecimal priceUsd,
        @Nonnull Double changePercent24Hr,
        @Nonnull BigDecimal vwap24Hr,
        @Nonnull String explorer,
        @Nullable Long timestamp) {
}
