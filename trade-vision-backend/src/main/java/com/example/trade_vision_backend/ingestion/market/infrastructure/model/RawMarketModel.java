package com.example.trade_vision_backend.ingestion.market.infrastructure.model;

import jakarta.persistence.*;

import java.util.UUID;


import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "raw_market_data")
@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID batchId;

    private String exchangeId;
    private Integer rank;
    private String baseSymbol;
    private String baseId;
    private String quoteSymbol;
    private String quoteId;
    private BigDecimal priceQuote;
    private BigDecimal priceUsd;
    private BigDecimal volumeUsd24Hr;
    private BigDecimal percentExchangeVolume;
    private Integer tradesCount24Hr;
    private Long updated;
    private Long timestamp;
}