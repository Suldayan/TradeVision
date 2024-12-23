package com.example.data_ingestion_service.services.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MarketDTO {
    String baseId;
    String quoteId;
    String exchangeId;
    BigDecimal currentPrice;
}
