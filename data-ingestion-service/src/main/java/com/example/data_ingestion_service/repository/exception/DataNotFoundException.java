package com.example.data_ingestion_service.repository.exception;

import jakarta.persistence.EntityNotFoundException;

public class DataNotFoundException extends EntityNotFoundException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
