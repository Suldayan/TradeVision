package com.example.data_processing_service.services;

public interface ConsumerService {
    void receiveStatus(String status);
    Long retrieveTimestamp();
}
