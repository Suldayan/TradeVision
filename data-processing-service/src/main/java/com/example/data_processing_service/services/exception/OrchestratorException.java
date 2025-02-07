package com.example.data_processing_service.services.exception;

public class OrchestratorException extends Exception {
    public OrchestratorException(String msg, Throwable e) {
        super(msg, e);
    }
}
