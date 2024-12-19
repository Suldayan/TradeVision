package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_market_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketModel {
    @Id
    @JsonProperty("baseId")
    private String id;

    @JsonProperty("rank")
    @JsonIgnore
    private Integer rank;

    @JsonProperty("exchangeId")
    private String exchangeId;

    @JsonProperty("quoteId")
    private String quoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    private RawExchangesModel exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_asset_id", nullable = false)
    private RawAssetModel baseAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_asset_id", nullable = false)
    private RawAssetModel quoteAsset;

    @Column(name = "base_symbol")
    @JsonProperty("baseSymbol")
    private String baseSymbol;

    @Column(name = "quote_symbol")
    @JsonProperty("quoteSymbol")
    private String quoteSymbol;

    @JsonProperty("priceQuote")
    private BigDecimal priceQuote;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("volumeUsd24Hr")
    private BigDecimal volumeUsd24Hr;

    @JsonProperty("percentExchangeVolume")
    private BigDecimal percentExchangeVolume;

    @JsonProperty("tradesCount24Hr")
    @Nullable
    private Integer tradesCount;

    @JsonProperty("updated")
    private Long updated;
}