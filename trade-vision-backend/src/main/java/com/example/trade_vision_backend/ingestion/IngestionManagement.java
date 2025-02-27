package com.example.trade_vision_backend.ingestion;

import com.example.trade_vision_backend.ingestion.internal.domain.dto.RawMarketDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionManagement {
    private final ApplicationEventPublisher eventPublisher;

    public void complete(Set<RawMarketDTO> data) {
        data.forEach(eventPublisher::publishEvent);
        log.info("Ingestion event sent");
    }
}
