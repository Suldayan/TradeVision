package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RawAssetHistoryWrapperModel {
    @JsonProperty
    private List<RawAssetHistoryModel> rawAssetHistoryModelList;

    @JsonProperty("timestamp")
    private BigDecimal timestamp;
}
