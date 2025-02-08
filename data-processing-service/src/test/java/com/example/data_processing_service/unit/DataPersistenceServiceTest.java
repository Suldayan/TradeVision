package com.example.data_processing_service.unit;

import com.example.data_processing_service.dto.RawMarketDTO;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.impl.DataPersistenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@DataJpaTest
@Import(DataPersistenceServiceImpl.class)
@ActiveProfiles("test")
public class DataPersistenceServiceTest {

    @Mock
    private MarketModelRepository marketModelRepository;

    @InjectMocks
    private DataPersistenceServiceImpl dataPersistenceService;

    private Set<RawMarketDTO> validMarketModels;
    private Set<RawMarketDTO> invalidMarketModels;

    private static final Long TIMESTAMP = 1737247412551L;

    @BeforeEach
    void setup() {
        validMarketModels = new HashSet<>();
        invalidMarketModels = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            RawMarketDTO model = RawMarketDTO.builder()
                    .modelId(UUID.randomUUID())
                    .baseId("BTC")
                    .rank(1)
                    .priceQuote(new BigDecimal("45000.50"))
                    .priceUsd(new BigDecimal("45000.50"))
                    .volumeUsd24Hr(new BigDecimal("300000000.00"))
                    .percentExchangeVolume(new BigDecimal("0.5"))
                    .tradesCount24Hr(100000)
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .baseSymbol("BTC")
                    .quoteSymbol("USDT")
                    .timestamp(TIMESTAMP)
                    .build();
            validMarketModels.add(model);
        }

        for (int i = 0; i < 5; i++) {
            RawMarketDTO model = RawMarketDTO.builder()
                    .modelId(UUID.randomUUID())
                    .baseId("BTC")
                    .rank(1)
                    .priceQuote(new BigDecimal("45000.50"))
                    .priceUsd(new BigDecimal("45000.50"))
                    .volumeUsd24Hr(new BigDecimal("300000000.00"))
                    .percentExchangeVolume(new BigDecimal("0.5"))
                    .tradesCount24Hr(100000)
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .baseSymbol("BTC")
                    .quoteSymbol("USDT")
                    .timestamp(TIMESTAMP)
                    .build();
            invalidMarketModels.add(model);
        }

        marketModelRepository.deleteAll();
    }


}
