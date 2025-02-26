package com.example.trade_vision_backend.ingestion;

import com.example.trade_vision_backend.ingestion.internal.domain.dto.RawMarketDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
public class IngestionCompleted extends ApplicationEvent {
    private final Set<RawMarketDTO> rawMarketDTOS;

    public IngestionCompleted(Object source, Set<RawMarketDTO> rawMarketDTOS) {
        super(source);
        this.rawMarketDTOS = rawMarketDTOS;
    }
}
