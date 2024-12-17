package com.example.data_ingestion_service.services.exceptions;

public class ApiException extends RuntimeException {
    public ApiException(String msg) {
        super(msg);
    }
}
