package com.example.data_ingestion_service.services.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String msg) {
        super(msg);
    }
    public ValidationException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
