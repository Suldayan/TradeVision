package com.example.data_ingestion_service.services.exceptions;

public class DataAggregateException extends RuntimeException {
    public DataAggregateException(String msg) {
        super(msg);
    }
}
