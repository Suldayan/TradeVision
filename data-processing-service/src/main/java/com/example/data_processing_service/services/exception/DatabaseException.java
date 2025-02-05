package com.example.data_processing_service.services.exception;

public class DatabaseException extends Exception {
    public DatabaseException(String msg, Throwable e) {
        super(msg, e);
    }
}
