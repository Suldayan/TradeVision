package com.example.data_ingestion_service.clients;

import com.example.data_ingestion_service.records.wrapper.ExchangeWrapper;
import org.springframework.web.service.annotation.GetExchange;

public interface ExchangeClient {
    @GetExchange(url = "/exchanges")
    ExchangeWrapper getExchanges();
}
