package com.example.data_ingestion_service.models.processed;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "asset")
public class AssetModel {
    @Id
    private String id;

    @Column(unique = true)
    private String symbol;

    private String name;
    private BigDecimal supply;
    private BigDecimal maxSupply;
    private BigDecimal marketCapUsd;
    private BigDecimal volumeUsd24Hr;
    private BigDecimal priceUsd;
    private BigDecimal changePercent24Hr;
    private BigDecimal vwap24Hr;

    @OneToMany(mappedBy = "baseAsset")
    @Nullable
    private Set<MarketModel> baseMarkets;

    @OneToMany(mappedBy = "quoteAsset")
    @Nullable
    private Set<MarketModel> quoteMarkets;

    public void addBaseMarket(@Nonnull MarketModel marketModel) {
        assert baseMarkets != null;
        baseMarkets.add(marketModel);
    }

    public void addQuoteMarket(@Nonnull MarketModel marketModel) {
        assert quoteMarkets != null;
        quoteMarkets.add(marketModel);
    }
}
