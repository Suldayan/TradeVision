package com.example.data_ingestion_service.models.processed;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "markets")
public class MarketModel {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    @Nullable
    private ExchangeModel exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_asset_id", nullable = false)
    @Nullable
    private AssetModel baseAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_asset_id", nullable = false)
    @Nullable
    private AssetModel quoteAsset;

    @Column(name = "base_symbol")
    private String baseSymbol;

    @Column(name = "quote_symbol")
    private String quoteSymbol;

    private BigDecimal priceQuote;
    private BigDecimal priceUsd;
    private BigDecimal volumeUsd24Hr;
    private BigDecimal percentExchangeVolume;
    private Integer tradesCount;
    private Long updated;

    @PrePersist
    @PreUpdate
    private void updateSymbols() {
        if (baseAsset != null) {
            this.baseSymbol = baseAsset.getSymbol();
        }
        if (quoteAsset != null) {
            this.quoteSymbol = quoteAsset.getSymbol();
        }
    }
}
