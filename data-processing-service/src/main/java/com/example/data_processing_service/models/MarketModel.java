package com.example.data_processing_service.models;

import com.example.data_processing_service.dto.RawMarketDTO;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
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

    @Column(name = "baseId", nullable = false)
    @NonNull
    private String baseId;

    @Column(name = "quoteId", nullable = false)
    @NonNull
    private String quoteId;

    @Column(name = "exchangeId", nullable = false)
    @Nonnull
    private String exchangeId;

    @Column(name = "priceUsd", nullable = false)
    @Nonnull
    private BigDecimal priceUsd;

    @Column(name = "updated", nullable = false)
    @Nonnull
    private Long updated;

    @Column(name = "timestamp", nullable = false)
    @Nonnull
    private ZonedDateTime timestamp;

    @Column(name = "createdAt", nullable = false)
    @Nonnull
    @CreationTimestamp
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketModel that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
