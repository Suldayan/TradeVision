package com.example.data_processing_service.models.processed;

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
    private ExchangesModel exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_asset_id", nullable = false)
    @Nullable
    private AssetModel baseAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_asset_id", nullable = false)
    @Nullable
    private AssetModel quoteAsset;

    private BigDecimal priceUsd;
    private Long updated;
}
