package com.example.data_processing_service.models.processed;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "markets")
public class MarketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // TODO configure an id identifier for the market (something to define what market it is)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id")
    @Nullable
    private ExchangesModel exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_asset_id")
    @Nullable
    private AssetModel baseAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_asset_id")
    @Nullable
    private AssetModel quoteAsset;

    private BigDecimal priceUsd;
    private Long updated;

    private Long timestamp;
    private Instant createdAt;
}
