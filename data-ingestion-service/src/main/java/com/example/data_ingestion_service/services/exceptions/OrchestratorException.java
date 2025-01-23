package com.example.data_ingestion_service.services.exceptions;

public class OrchestratorException extends Exception {
    public OrchestratorException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
