package com.example.trade_strategy_engine.features.strategies.sma.service;

import java.util.List;

public interface SimpleMovingAverageService {
    List<Integer> getClosingPricePerDay();
    Integer calculateAverageOverInterval();
}
