package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Builder
public class MarketResponseModel {
    @JsonProperty("data")
    @Size(min = 1, message = "Market list cannot be empty")
    @Valid
    private List<MarketModel> markets;

    @JsonProperty("timestamp")
    @NotNull(message = "Timestamp cannot be null")
    private String timestamp;
}
