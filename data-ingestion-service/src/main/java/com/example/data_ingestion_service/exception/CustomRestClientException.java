package com.example.data_ingestion_service.exception;

import org.springframework.web.client.RestClientException;

public class CustomRestClientException extends RestClientException {
    public CustomRestClientException(String msg) {
        super(msg);
    }
}
