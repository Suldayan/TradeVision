package com.example.trade_vision_backend.ingestion.core.event;

import com.example.trade_vision_backend.ingestion.infrastructure.dto.RawMarketDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.modulith.NamedInterface;

import java.util.Set;

@Getter
@NamedInterface("event")
public class IngestionCompleted extends ApplicationEvent {
    private final Set<RawMarketDTO> rawMarketDTOS;

    public IngestionCompleted(Object source, Set<RawMarketDTO> rawMarketDTOS) {
        super(source);
        this.rawMarketDTOS = rawMarketDTOS;
    }
}
