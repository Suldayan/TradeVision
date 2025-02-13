package com.example.data_processing_service.database.service.exception;

public class DatabaseException extends Exception {
    public DatabaseException(String msg) {
        super(msg);
    }
    public DatabaseException(String msg, Throwable e) {
        super(msg, e);
    }
}
