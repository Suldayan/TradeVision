package com.example.data_processing_service.features.orchestrator.exception;

public class OrchestratorException extends Exception {
    public OrchestratorException(String msg, Throwable e) {
        super(msg, e);
    }
}
