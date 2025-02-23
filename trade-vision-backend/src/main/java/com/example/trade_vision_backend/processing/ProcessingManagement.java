package com.example.trade_vision_backend.processing;

import com.example.trade_vision_backend.ingestion.core.event.IngestionCompleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProcessingManagement {

    @ApplicationModuleListener
    public void on(IngestionCompleted event) {
        log.info("Ingestion event received");
    }
}
