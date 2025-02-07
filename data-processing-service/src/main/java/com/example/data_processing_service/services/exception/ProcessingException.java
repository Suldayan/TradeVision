package com.example.data_processing_service.services.exception;

public class ProcessingException extends Exception {
    public ProcessingException(String msg) {
        super(msg);
    }
    public ProcessingException(String msg, Throwable e) {
        super(msg, e);
    }
}
