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

    @Column(name = "exchange_id", nullable = false)
    @Nonnull
    private String exchangeId;

    @Column(name = "name", nullable = false)
    @Nonnull
    private String name;

    @Column(name = "rank", nullable = false)
    @Nonnull
    private Integer rank;

    @Column(name = "percent_total_volume", nullable = false)
    @Nullable
    private BigDecimal percentTotalVolume;

    @Column(name = "volume_usd")
    @Nullable
    private BigDecimal volumeUsd;

    @Column(name = "trading_pairs", nullable = false)
    @Nonnull
    private Integer tradingPairs;

    @Column(name = "socket")
    private boolean socket;

    @Column(name = "exchange_url", nullable = false)
    @Nonnull
    private String exchangeUrl;

    @Column(name = "updated", nullable = false)
    @Nonnull
    private Long updated;
}