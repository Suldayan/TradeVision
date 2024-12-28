package com.example.data_ingestion_service.models.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawAssetModel {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("rank")
    @JsonIgnore
    private Integer rank;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("supply")
    private BigDecimal supply;

    @JsonProperty("maxSupply")
    @Nullable
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

    @JsonProperty("explorer")
    @JsonIgnore
    private String explorer;
}