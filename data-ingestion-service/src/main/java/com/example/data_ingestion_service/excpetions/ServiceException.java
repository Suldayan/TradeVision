package com.example.data_ingestion_service.excpetions;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
