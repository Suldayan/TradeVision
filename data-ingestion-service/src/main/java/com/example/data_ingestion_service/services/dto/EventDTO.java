package com.example.data_ingestion_service.services.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EventDTO {
    @Nonnull
    @NotBlank(message = "Status can't be blank")
    private String status;

    @Nonnull
    @DecimalMin(value = "0.0")
    private Long timestamp;
}