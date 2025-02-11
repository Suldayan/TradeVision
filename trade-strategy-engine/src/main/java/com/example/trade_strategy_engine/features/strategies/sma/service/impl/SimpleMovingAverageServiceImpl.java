package com.example.trade_strategy_engine.features.strategies.sma.service.impl;

import com.example.trade_strategy_engine.features.strategies.sma.service.SimpleMovingAverageService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleMovingAverageServiceImpl implements SimpleMovingAverageService {

    @Nonnull
    @Override
    public List<Integer> getClosingPricePerDay() {
        return List.of();
    }

    @Nonnull
    @Override
    public Integer calculateAverageOverInterval() {
        return 0;
    }
}
