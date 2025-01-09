package com.example.data_ingestion_service.records;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record Exchange(@Nonnull String exchangeId,
                       @Nonnull String name,
                       @Nonnull Integer rank,
                       @Nonnull BigDecimal percentTotalVolume,
                       @Nonnull BigDecimal volumeUsd,
                       @Nonnull Integer tradingPairs,
                       @Nonnull Boolean socket,
                       @Nonnull String exchangeUrl,
                       @Nonnull Long updated) {
}
