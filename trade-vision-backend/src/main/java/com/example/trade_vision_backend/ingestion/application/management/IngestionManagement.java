package com.example.trade_vision_backend.ingestion.application.management;

import com.example.trade_vision_backend.ingestion.core.event.IngestionCompleted;
import com.example.trade_vision_backend.ingestion.infrastructure.dto.RawMarketDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionManagement {
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void complete(Set<RawMarketDTO> data) {
        eventPublisher.publishEvent(new IngestionCompleted(this, data));
        log.info("Ingestion event sent");
    }
}
