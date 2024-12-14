package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RawExchangeWrapperModel {
    @JsonProperty("data")
    private List<RawExchangesModel> rawExchangesModelList;

    @JsonProperty("timestamp")
    private BigDecimal timestamp;
}
