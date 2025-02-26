package com.example.trade_vision_backend.ingestion.integration;

import com.example.trade_vision_backend.ingestion.internal.domain.IngestionService;
import com.example.trade_vision_backend.ingestion.internal.domain.dto.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IngestionServiceTest {

    @Autowired
    private IngestionService ingestionService;

    @Test
    void 

    private static Set<RawMarketDTO> createValidMarketDTOs() {
        Set<RawMarketDTO> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            set.add(new RawMarketDTO(
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00"),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200,
                    1696252800000L,
                    null
            ));
        }

        return set;
    }
}
