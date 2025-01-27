package com.example.data_ingestion_service.controller.exception;

import jakarta.persistence.EntityNotFoundException;

public class DataNotFoundException extends EntityNotFoundException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
