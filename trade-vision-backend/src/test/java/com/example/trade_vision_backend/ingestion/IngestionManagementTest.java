package com.example.trade_vision_backend.ingestion;

import com.example.trade_vision_backend.ingestion.internal.application.IngestionService;
import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ApplicationModuleTest
@ActiveProfiles("test")
public class IngestionManagementTest {

    @MockitoBean
    private IngestionManagement ingestionManagement;

    @Autowired
    private IngestionService ingestionService;

    @Test
    void getMarketData_ShouldSuccessfullyRunFullIngestionFlowAndPublishEvent(Scenario scenario) {
        scenario.stimulate(() -> ingestionService.executeIngestion())
                .andWaitForEventOfType(IngestionCompleted.class)
                .toArriveAndVerify(event -> {
                    assertNotNull(event);
                });
    }

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
