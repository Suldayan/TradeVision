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

    @Nonnull
    private String exchangeId;

    @Nonnull
    private Integer rank;

    @Nonnull
    private String baseSymbol;

    @Nonnull
    private String baseId;

    @Nonnull
    private String quoteSymbol;

    @Nonnull
    private String quoteId;

    @Nonnull
    private BigDecimal priceQuote;

    @Nonnull
    private BigDecimal priceUsd;

    @Nonnull
    private BigDecimal volumeUsd24Hr;

    @Nonnull
    private BigDecimal percentExchangeVolume;

    @Nullable
    private Integer tradesCount24Hr;

    @Nonnull
    private Long updated;
}