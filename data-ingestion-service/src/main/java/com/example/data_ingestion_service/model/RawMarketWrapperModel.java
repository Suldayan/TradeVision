package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RawMarketWrapperModel {
    @JsonProperty("data")
    private List<RawMarketModel> marketModelList;

    @JsonProperty("timestamp")
    private Long timestamp;
}
