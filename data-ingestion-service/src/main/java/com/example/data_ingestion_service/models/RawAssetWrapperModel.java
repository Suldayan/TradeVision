package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawAssetWrapperModel {
    @JsonProperty("data")
    private List<RawAssetModel> rawAssetModelList;

    @JsonProperty("timestamp")
    private BigDecimal timestamp;
}
