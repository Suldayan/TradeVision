package com.example.data_ingestion_service.models;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
// TODO configure columns and better validation for models
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

    @Nonnull
    private String id;

    @Nonnull
    private Integer rank;

    @Nonnull
    private String symbol;

    @Nonnull
    private String name;

    @Nonnull
    private BigDecimal supply;

    @Nullable
    private BigDecimal maxSupply;

    @Nonnull
    private BigDecimal marketCapUsd;

    @Nonnull
    private BigDecimal volumeUsd24Hr;

    @Nonnull
    private BigDecimal priceUsd;

    @Nonnull
    private BigDecimal changePercent24Hr;

    @Nonnull
    private BigDecimal vwap24Hr;

    @Nullable
    private String explorer;
}