package com.example.data_processing_service.services.exception;

import com.example.data_processing_service.services.ProcessingOrchestratorService;

public class ProcessingException extends Exception {
    public ProcessingException(String msg) {
        super(msg);
    }
    public ProcessingException(String msg, Throwable e) {
        super(msg, e);
    }
}
