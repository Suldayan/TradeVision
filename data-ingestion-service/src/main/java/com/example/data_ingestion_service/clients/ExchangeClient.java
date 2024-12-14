package com.example.data_ingestion_service.clients;

import com.example.data_ingestion_service.models.RawExchangeWrapperModel;
import org.springframework.web.service.annotation.GetExchange;

public interface ExchangeClient {
    @GetExchange(url = "/exchange")
    RawExchangeWrapperModel getExchanges();
}
