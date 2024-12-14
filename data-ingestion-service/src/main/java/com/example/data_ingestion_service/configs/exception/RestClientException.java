package com.example.data_ingestion_service.configs.exception;

public class RestClientException extends org.springframework.web.client.RestClientException {
    public RestClientException(String msg) {
        super(msg);
    }
}
