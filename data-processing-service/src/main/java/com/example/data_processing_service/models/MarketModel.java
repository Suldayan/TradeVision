package com.example.data_processing_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    @Column(name = "priceUsd", nullable = false)
    private BigDecimal priceUsd;

    @Column(name = "updated", nullable = false)
    private Long updated;

    @Column(name = "timestamp", nullable = false)
    private ZonedDateTime timestamp;

    @Column(name = "createdAt", nullable = false)
    private Instant createdAt;
}
