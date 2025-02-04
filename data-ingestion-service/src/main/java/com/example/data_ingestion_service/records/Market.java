package com.example.data_ingestion_service.records;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Market(@Nonnull String exchangeId,
                     @Nonnull Integer rank,
                     @Nonnull String baseSymbol,
                     @Nonnull String baseId,
                     @Nonnull String quoteSymbol,
                     @Nonnull String quoteId,
                     @Nonnull BigDecimal priceQuote,
                     @Nonnull BigDecimal priceUsd,
                     @Nonnull BigDecimal volumeUsd24Hr,
                     @Nonnull BigDecimal percentExchangeVolume,
                     @Nullable Integer tradesCount24Hr,
                     @Nonnull Long updated,
                     @Nullable Long timestamp) {
}
