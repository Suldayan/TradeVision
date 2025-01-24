package com.example.data_processing_service.services;

import com.example.data_processing_service.dto.EventDTO;

public interface ConsumerService {
    void receiveStatus(EventDTO status);
}
