package com.example.trade_vision_backend.ingestion.internal.application;

import com.example.trade_vision_backend.ingestion.IngestionManagement;
import com.example.trade_vision_backend.ingestion.internal.application.exception.IngestionException;
import com.example.trade_vision_backend.ingestion.market.application.service.MarketService;
import com.example.trade_vision_backend.ingestion.market.domain.dto.MarketWrapperDTO;
import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import com.example.trade_vision_backend.ingestion.market.infrastructure.model.RawMarketModel;
import com.example.trade_vision_backend.ingestion.internal.infrastructure.repository.IngestionRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionServiceImpl implements IngestionService {
    private final MarketService marketService;
    private final IngestionRepository ingestionRepository;
    private final IngestionManagement ingestionManagement;

    @Override
    public void sendEvent(@Nonnull Set<RawMarketDTO> marketDTOS) {
        ingestionManagement.complete(marketDTOS);
    }

    @Scheduled(fixedRate = 900000)
    @Override
    @Transactional
    public void executeIngestion() {
        try {
            MarketWrapperDTO marketWrapper = marketService.getMarketsData();
            Set<RawMarketDTO> rawMarketDTOS = marketService.convertWrapperDataToRecord(marketWrapper);
            Set<RawMarketModel> rawMarketModels = marketService.rawMarketDTOToModel(rawMarketDTOS);
            saveMarketData(rawMarketModels);
            sendEvent(rawMarketDTOS);
            log.info("Successfully completed market ingestion flow");
        } catch (Exception ex) {
            throw new RestClientException("Market ingestion failed due to unexpected error", ex);
        }
    }

    // TODO configure a retry
    @Transactional
    private void saveMarketData(@Nonnull Set<RawMarketModel> data) throws IngestionException {
        try {
            ingestionRepository.saveAll(data);
            log.info("Successfully saved all data entries");
        } catch (DataAccessException ex) {
            throw new IngestionException("Database error occurred while saving market data", ex);
        } catch (Exception ex) {
            throw new IngestionException("An unexpected error occurred while saving raw market data", ex);
        }
    }
}