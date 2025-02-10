package com.example.trade_strategy_engine.features.shared.client;

import com.example.trade_strategy_engine.features.shared.record.MarketRecord;
import org.springframework.web.service.annotation.GetExchange;

import java.time.ZonedDateTime;
import java.util.Set;

public interface ProcessingClient {

    @GetExchange("/markets")
    Set<MarketRecord> getAllMarketData(
            ZonedDateTime start,
            ZonedDateTime end
    );

    @GetExchange("/market/base")
    Set<MarketRecord> getMarketDataByBaseId(
            String baseId,
            ZonedDateTime start,
            ZonedDateTime end
    );

    @GetExchange("/market/quote")
    Set<MarketRecord> getMarketDataByQuoteId(
            String quoteId,
            ZonedDateTime start,
            ZonedDateTime end
    );

    @GetExchange("/market/exchange")
    Set<MarketRecord> getMarketDataByExchangeId(
            String exchangeId,
            ZonedDateTime start,
            ZonedDateTime end
    );
}
