package com.example.data_ingestion_service.models;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "raw_markets",
        indexes = {@Index(name = "idx_timestamp", columnList = "timestamp")}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID modelId;

    @Column(name = "exchange_id", nullable = false)
    @Nonnull
    @NotBlank(message = "Exchange ID cannot be blank")
    private String exchangeId;

    @Column(name = "rank", nullable = false)
    @Nonnull
    @Min(value = 1, message = "Rank must be positive")
    private Integer rank;

    @Column(name = "base_symbol", nullable = false)
    @Nonnull
    @Size(min = 1, max = 10, message = "Base symbol must be between 1 and 10 characters")
    private String baseSymbol;

    @Column(name = "base_id", nullable = false)
    @Nonnull
    @Size(min = 1, max = 30, message = "Base ID must be between 1 and 30 characters")
    private String baseId;

    @Column(name = "quote_symbol", nullable = false)
    @Nonnull
    @Size(min = 1, max = 10, message = "Quote symbol must be between 1 and 10 characters")
    private String quoteSymbol;

    @Column(name = "quote_id", nullable = false)
    @Nonnull
    @Size(min = 1, max = 20, message = "Quote ID must be between 1 and 20 characters")
    private String quoteId;

    @Column(name = "price_quote", nullable = false, precision = 30, scale = 8)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Price quote must be non-negative")
    private BigDecimal priceQuote;

    @Column(name = "price_usd", nullable = false, precision = 30, scale = 8)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Price usd must be non-negative")
    private BigDecimal priceUsd;

    @Column(name = "volume_usd_24hr", nullable = false, precision = 30, scale = 2)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Volume usd 24hr volume must be non-negative")
    private BigDecimal volumeUsd24Hr;

    @Column(name = "percent_exchange_volume", nullable = false, precision = 10, scale = 4)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Percent exchange volume must be non-negative")
    private BigDecimal percentExchangeVolume;

    @Column(name = "trades_count_24hr")
    @Nullable
    @Min(value = 0, message = "Trades count must be non-negative")
    private Integer tradesCount24Hr;

    @Column(name = "updated", nullable = false)
    @Nonnull
    @Min(value = 0, message = "Updated must be non-negative")
    private Long updated;

    @Column(name = "timestamp", nullable = false)
    @Nullable
    @Min(value = 0, message = "Timestamp must be non-negative")
    private Long timestamp;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawMarketModel that)) return false;
        return modelId != null && modelId.equals(that.modelId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}