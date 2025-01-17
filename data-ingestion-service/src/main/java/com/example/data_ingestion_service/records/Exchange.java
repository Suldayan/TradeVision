package com.example.data_ingestion_service.records;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;

public record Exchange(@Nonnull String exchangeId,
                       @Nonnull String name,
                       @Nonnull Integer rank,
                       @Nullable BigDecimal percentTotalVolume,
                       @Nullable BigDecimal volumeUsd,
                       @Nonnull Integer tradingPairs,
                       @Nullable Boolean socket,
                       @Nonnull String exchangeUrl,
                       @Nonnull Long updated) {
}
