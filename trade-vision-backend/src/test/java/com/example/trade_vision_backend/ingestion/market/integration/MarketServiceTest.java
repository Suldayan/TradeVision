package com.example.trade_vision_backend.ingestion.market.integration;

import com.example.trade_vision_backend.ingestion.market.MarketService;
import com.example.trade_vision_backend.ingestion.market.domain.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MarketServiceTest {

    @Autowired
    private MarketService marketService;

    @Test
    void getMarketsData_SuccessfullyReturnsMarketWrapperDTO() {
        MarketWrapperDTO result = assertDoesNotThrow(
                () -> marketService.getMarketsData()
        );

        Set<RawMarketDTO> resultSet = result.markets();

        assertNotNull(result);
        assertFalse(resultSet.isEmpty());
        assertEquals(100, resultSet.size());
    }
}
