package com.example.data_processing_service.features.ingestion.exception;

public class IngestionException extends Exception {
    public IngestionException(String msg, Throwable e) {
        super(msg, e);
    }
}
