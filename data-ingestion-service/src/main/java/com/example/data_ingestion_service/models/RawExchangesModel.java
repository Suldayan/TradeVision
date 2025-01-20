package com.example.data_ingestion_service.models;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "raw_exchanges",
        indexes = {@Index(name = "idx_timestamp", columnList = "timestamp")}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawExchangesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID modelId;

    @Column(name = "exchange_id", nullable = false)
    @Nonnull
    @NotBlank(message = "Exchange ID cannot be blank")
    private String exchangeId;

    @Column(name = "name", nullable = false)
    @Nonnull
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column(name = "rank", nullable = false)
    @Nonnull
    @Min(value = 1, message = "Rank must be positive")
    private Integer rank;

    @Column(name = "percent_total_volume", precision = 10, scale = 4)
    @Nullable
    private BigDecimal percentTotalVolume;

    @Column(name = "volume_usd", precision = 30, scale = 2)
    @Nullable
    private BigDecimal volumeUsd;

    @Column(name = "trading_pairs", nullable = false)
    @Nonnull
    @Min(value = 0, message = "Trading pairs must be non-negative")
    private Integer tradingPairs;

    @Column(name = "socket")
    private boolean socket;

    @Column(name = "exchange_url", nullable = false)
    @Nonnull
    @URL(message = "Exchange url must be a valid")
    private String exchangeUrl;

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
        if (!(o instanceof RawExchangesModel that)) return false;
        return modelId != null && modelId.equals(that.modelId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}