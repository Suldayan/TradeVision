package com.example.data_processing_service.services.exception;

import java.time.Instant;

public class EventProcessingException extends RuntimeException {
    public EventProcessingException(String msg, Long timestamp, Throwable e) {
        super();
    }
}
