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
@Table(name = "raw_exchanges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawExchangesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String modelId;

    @Nonnull
    private String exchangeId;

    @Nonnull
    private String name;

    @Nonnull
    private Integer rank;

    @Nullable
    private BigDecimal percentTotalVolume;

    @Nullable
    private BigDecimal volumeUsd;

    @Nonnull
    private Integer tradingPairs;

    private boolean socket;

    @Nonnull
    private String exchangeUrl;

    @Nonnull
    private Long updated;
}