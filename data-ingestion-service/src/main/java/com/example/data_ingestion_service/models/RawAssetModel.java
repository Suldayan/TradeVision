package com.example.data_ingestion_service.models;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "raw_assets",
        indexes = {
                @Index(name = "idx_symbol", columnList = "symbol"),
                @Index(name = "idx_rank", columnList = "rank"),
                @Index(name = "idx_asset_id", columnList = "id", unique = true)
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RawAssetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String modelId;

    @Column(name = "id", nullable = false)
    @Nonnull
    @NotBlank(message = "Asset ID cannot be blank")
    private String id;

    @Column(name = "rank", nullable = false)
    @Nonnull
    @Min(value = 1, message = "Rank must be positive")
    private Integer rank;

    @Column(name = "symbol", nullable = false, length = 10)
    @Nonnull
    @NotBlank(message = "Symbol cannot be blank")
    @Size(min = 1, max = 10, message = "Symbol must be between 1 and 10 characters")
    private String symbol;

    @Column(name = "name", nullable = false, length = 100)
    @Nonnull
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Column(name = "supply", nullable = false, precision = 30, scale = 8)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Supply must be non-negative")
    private BigDecimal supply;

    @Column(name = "max_supply", precision = 30, scale = 8)
    @Nullable
    @DecimalMin(value = "0.0", message = "Max supply must be non-negative")
    private BigDecimal maxSupply;

    @Column(name = "market_cap_usd", nullable = false, precision = 30, scale = 2)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Market cap must be non-negative")
    private BigDecimal marketCapUsd;

    @Column(name = "volume_usd_24hr", nullable = false, precision = 30, scale = 2)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Volume must be non-negative")
    private BigDecimal volumeUsd24Hr;

    @Column(name = "price_usd", nullable = false, precision = 30, scale = 8)
    @Nonnull
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal priceUsd;

    @Column(name = "change_percent_24hr", nullable = false, precision = 10, scale = 2)
    @Nonnull
    private BigDecimal changePercent24Hr;

    @Column(name = "vwap_24hr", nullable = false, precision = 30, scale = 8)
    @Nonnull
    @DecimalMin(value = "0.0", message = "VWAP must be non-negative")
    private BigDecimal vwap24Hr;

    @Column(name = "explorer")
    @Nullable
    @URL(message = "Explorer must be a valid URL")
    private String explorer;

    @Column(name = "timestamp")
    @Nonnull
    private Long timestamp;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawAssetModel that)) return false;
        return modelId != null && modelId.equals(that.modelId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}