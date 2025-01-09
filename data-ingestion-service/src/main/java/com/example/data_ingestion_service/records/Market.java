package com.example.data_ingestion_service.records;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record Market(@Nonnull String exchangeId,
                     @Nonnull Integer rank,
                     @Nonnull String baseSymbol,
                     @Nonnull String quoteSymbol,
                     @Nonnull String quoteId,
                     @Nonnull BigDecimal priceQuote,
                     @Nonnull BigDecimal priceUsd,
                     @Nonnull BigDecimal volumeUsd24Hr,
                     @Nonnull BigDecimal percentExchangeVolume,
                     @Nonnull Integer tradesCount24Hr,
                     @Nonnull Long updated) {
}
