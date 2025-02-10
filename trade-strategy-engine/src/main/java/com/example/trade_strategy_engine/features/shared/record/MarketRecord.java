package com.example.trade_strategy_engine.features.shared.record;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Builder
public record MarketRecord(
        @Nonnull String baseId,
        @Nonnull String quoteId,
        @Nonnull String exchangeId,
        @Nonnull BigDecimal priceUsd,
        @Nonnull Long updated,
        @Nonnull ZonedDateTime timestamp
) {}
