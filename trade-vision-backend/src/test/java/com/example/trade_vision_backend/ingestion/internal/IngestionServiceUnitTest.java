package com.example.trade_vision_backend.ingestion.internal;

import com.example.trade_vision_backend.ingestion.IngestionManagement;
import com.example.trade_vision_backend.ingestion.internal.infrastructure.service.IngestionServiceImpl;
import com.example.trade_vision_backend.ingestion.internal.infrastructure.repository.IngestionRepository;
import com.example.trade_vision_backend.ingestion.market.MarketService;
import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import com.example.trade_vision_backend.ingestion.market.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class IngestionServiceUnitTest {

    @Mock
    private MarketService marketService;

    @Mock
    private IngestionRepository ingestionRepository;

    @Mock
    private IngestionManagement ingestionManagement;

    @InjectMocks
    private IngestionServiceImpl ingestionService;

    @Test
    void saveMarketData_UpdatesAndSavesExistingDataSuccessfully() {
        List<RawMarketModel> updatedModels = createUpdatedValidMarketModelList();
        ingestionRepository.saveAll(createValidMarketModelList());
        ingestionRepository.flush();

        assertDoesNotThrow(() ->
                ingestionService.saveMarketData(updatedModels));

        List<RawMarketModel> result = ingestionRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(100, result.size());
        assertEquals(updatedModels, result);
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

    private static List<RawMarketModel> createValidMarketModelList() {
        List<RawMarketModel> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new RawMarketModel(
                    UUID.randomUUID(),
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00").add(new BigDecimal(i)),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200 + i,
                    1696252800000L + i,
                    null
            ));
        }

        return list;
    }

    private static List<RawMarketModel> createUpdatedValidMarketModelList() {
        List<RawMarketModel> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new RawMarketModel(
                    UUID.randomUUID(),
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("25000.00").add(new BigDecimal(i)),
                    new BigDecimal("45000.00"),
                    new BigDecimal("9500000000.00"),
                    new BigDecimal("10.25"),
                    3200 + i,
                    1732252800000L + i,
                    null
            ));
        }

        return list;
    }
}
