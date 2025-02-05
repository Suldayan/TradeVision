package com.example.data_processing_service.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class IngestionException extends RuntimeException {
    public IngestionException(String msg) {
        super(msg);
    }

    public IngestionException(String msg, Throwable e) {
        super(msg, e);
    }
}
