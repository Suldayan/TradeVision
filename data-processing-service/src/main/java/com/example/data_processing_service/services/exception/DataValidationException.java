package com.example.data_processing_service.services.exception;

public class DataValidationException extends Exception {
    public DataValidationException(String msg) {
        super(msg);
    }
    public DataValidationException(String msg, Throwable e) {
        super(msg, e);
    }
}
