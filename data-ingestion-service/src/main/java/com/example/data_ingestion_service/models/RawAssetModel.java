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
@Table(name = "raw_assets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawAssetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String modelId;

    @Column(name = "id", nullable = false)
    @Nonnull
    private String id;

    @Column(name = "rank", nullable = false)
    @Nonnull
    private Integer rank;

    @Column(name = "symbol", nullable = false)
    @Nonnull
    private String symbol;

    @Column(name = "name", nullable = false)
    @Nonnull
    private String name;

    @Column(name = "supply", nullable = false)
    @Nonnull
    private BigDecimal supply;

    @Column(name = "max_supply")
    @Nullable
    private BigDecimal maxSupply;

    @Column(name = "market_cap_usd", nullable = false)
    @Nonnull
    private BigDecimal marketCapUsd;

    @Column(name = "volume_usd_24hr", nullable = false)
    @Nonnull
    private BigDecimal volumeUsd24Hr;

    @Column(name = "price_usd", nullable = false)
    @Nonnull
    private BigDecimal priceUsd;

    @Column(name = "change_percent_24hr", nullable = false)
    @Nonnull
    private BigDecimal changePercent24Hr;

    @Column(name = "vwap_24hr", nullable = false)
    @Nonnull
    private BigDecimal vwap24Hr;

    @Column(name = "explorer")
    @Nullable
    private String explorer;
}