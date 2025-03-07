package com.example.trade_vision_backend.processing;

import jakarta.persistence.*;
import lombok.*;

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
@EqualsAndHashCode(of = {"baseId", "quoteId", "exchangeId"})
public class ProcessedMarketModel {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(name = "baseId", nullable = false)
        private String baseId;

        @Column(name = "quoteId", nullable = false)
        private String quoteId;

        @Column(name = "exchangeId", nullable = false)
        private String exchangeId;

        @Column(name = "priceUsd", nullable = false)
        private BigDecimal priceUsd;

        @Column(name = "updated", nullable = false)
        private Long updated;

        @Column(name = "timestamp", nullable = false)
        private ZonedDateTime timestamp;

        @Column(name = "createdAt")
        private Instant createdAt;
}
