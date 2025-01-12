package com.example.data_ingestion_service.services.exceptions;

public class AsyncException extends RuntimeException {
    public AsyncException(String msg) {
        super(msg);
    }

    public AsyncException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
