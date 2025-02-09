package com.example.data_processing_service.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketDTO {

    @Nonnull
    @NotBlank(message = "Exchange ID cannot be blank")
    private String exchangeId;

    @Nonnull
    @Min(value = 1, message = "Rank must be positive")
    private Integer rank;

    @Nonnull
    @Size(min = 1, max = 10, message = "Base symbol must be between 1 and 10 characters")
    private String baseSymbol;

    @Nonnull
    @Size(min = 1, max = 30, message = "Base ID must be between 1 and 30 characters")
    private String baseId;

    @Nonnull
    @Size(min = 1, max = 10, message = "Quote symbol must be between 1 and 10 characters")
    private String quoteSymbol;

    @Nonnull
    @Size(min = 1, max = 20, message = "Quote ID must be between 1 and 20 characters")
    private String quoteId;

    @Nonnull
    @DecimalMin(value = "0.0", message = "Price quote must be non-negative")
    private BigDecimal priceQuote;

    @Nonnull
    @DecimalMin(value = "0.0", message = "Price usd must be non-negative")
    private BigDecimal priceUsd;

    @Nonnull
    @DecimalMin(value = "0.0", message = "Volume usd 24hr volume must be non-negative")
    private BigDecimal volumeUsd24Hr;

    @Nonnull
    @DecimalMin(value = "0.0", message = "Percent exchange volume must be non-negative")
    private BigDecimal percentExchangeVolume;

    @Nonnull
    @Min(value = 0, message = "Trades count must be non-negative")
    private Integer tradesCount24Hr;

    @Nonnull
    @Min(value = 0, message = "Updated must be non-negative")
    private Long updated;

    @Nonnull
    @Min(value = 0, message = "Timestamp must be non-negative")
    private Long timestamp;

    @Nonnull
    @CreationTimestamp
    private Instant createdAt;
}
