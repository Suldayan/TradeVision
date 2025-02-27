package com.example.trade_vision_backend.ingestion;

import com.example.trade_vision_backend.ingestion.internal.domain.dto.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationModuleTest
@ActiveProfiles("test")
public class IngestionManagementTest {

    @MockitoBean
    private IngestionManagement ingestionManagement;

    @Test
    void shouldPublishIngestionCompletedEvent(Scenario scenario) {
        Set<RawMarketDTO> marketDTOS = createValidMarketDTOs();

        scenario.stimulate(() -> ingestionManagement.complete(marketDTOS))
                .andWaitForEventOfType(IngestionCompleted.class)
                .matching(event -> {
                    Set<RawMarketDTO> eventData = event.getRawMarketDTOS();
                    return eventData.equals(marketDTOS);
                })
                .toArriveAndVerify(event -> {
                    assertNotNull(event);
                    assertFalse(event.getRawMarketDTOS().isEmpty());
                    assertEquals(marketDTOS, event.getRawMarketDTOS());
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
