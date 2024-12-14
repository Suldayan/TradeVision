package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_asset_data")
public class RawAssetModel {
    @Id
    @JsonProperty("id")
    private String id;

    @Column(unique = true)
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("supply")
    private BigDecimal supply;

    @JsonProperty("maxSupply")
    private BigDecimal maxSupply;

    @JsonProperty("marketCapUsd")
    private BigDecimal marketCapUsd;

    @JsonProperty("volumeUsd24Hr")
    private BigDecimal volumeUsd24Hr;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("changePercent24Hr")
    private BigDecimal changePercent24Hr;

    @JsonProperty("vwap24Hr")
    private BigDecimal vwap24Hr;

    @OneToMany(mappedBy = "baseAsset")
    private Set<RawMarketModel> baseMarkets;

    @OneToMany(mappedBy = "quoteAsset")
    private Set<RawMarketModel> quoteMarkets;
}