package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exchange_id")
    @JsonProperty("exchangeId")
    private String exchangeId;

    @Column(name = "base_id")
    @JsonProperty("baseId")
    private String baseId;

    @Column(name = "base_symbol")
    @JsonProperty("baseSymbol")
    private String baseSymbol;

    @Column(name = "quote_symbol")
    @JsonProperty("quoteSymbol")
    private String quoteSymbol;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("volumeUsd24Hr")
    private BigDecimal volumeUsd24Hr;

    @JsonProperty("percentExchangeVolume")
    private BigDecimal percentExchangeVolume;

    @JsonProperty("updated")
    private Long updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RawExchangesModel exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id", referencedColumnName = "history", insertable = false, updatable = false)
    private RawAssetHistoryModel assetHistory;
}
