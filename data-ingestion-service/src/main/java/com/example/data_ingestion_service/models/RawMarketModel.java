package com.example.data_ingestion_service.models;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_markets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String modelId;

    @Column(name = "exchange_id", nullable = false)
    @Nonnull
    private String exchangeId;

    @Column(name = "rank", nullable = false)
    @Nonnull
    private Integer rank;

    @Column(name = "base_symbol", nullable = false)
    @Nonnull
    private String baseSymbol;

    @Column(name = "base_id", nullable = false)
    @Nonnull
    private String baseId;

    @Column(name = "quote_symbol", nullable = false)
    @Nonnull
    private String quoteSymbol;

    @Column(name = "quote_id", nullable = false)
    @Nonnull
    private String quoteId;

    @Column(name = "price_quote", nullable = false)
    @Nonnull
    private BigDecimal priceQuote;

    @Column(name = "price_usd", nullable = false)
    @Nonnull
    private BigDecimal priceUsd;

    @Column(name = "volume_usd_24hr", nullable = false)
    @Nonnull
    private BigDecimal volumeUsd24Hr;

    @Column(name = "percent_exchange_volume", nullable = false)
    @Nonnull
    private BigDecimal percentExchangeVolume;

    @Column(name = "trades_count_24hr")
    @Nullable
    private Integer tradesCount24Hr;

    @Column(name = "updated", nullable = false)
    @Nonnull
    private Long updated;
}