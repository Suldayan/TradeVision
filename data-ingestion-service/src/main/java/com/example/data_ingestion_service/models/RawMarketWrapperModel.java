package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketWrapperModel {
    @JsonProperty("data")
    private List<RawMarketModel> marketModelList;

    @JsonProperty("timestamp")
    private Long timestamp;
}
