package com.example.data_processing_service.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class EventDTO {
    @Nonnull
    @NotBlank(message = "Status can't be blank")
    private String status;

    @Nonnull
    @DecimalMin(value = "0.0")
    private Long timestamp;
}
