package com.example.trade_vision_backend.ingestion;

import com.example.trade_vision_backend.ingestion.market.RawMarketDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionManagement {
    private final ApplicationEventPublisher eventPublisher;

    public void complete(Set<RawMarketDTO> data) {
        eventPublisher.publishEvent(new IngestionCompleted(
                UUID.randomUUID(),
                data.size(),
                data.iterator().next().timestamp(),
                this
        ));
        log.info("Ingestion event sent");
    }
}
