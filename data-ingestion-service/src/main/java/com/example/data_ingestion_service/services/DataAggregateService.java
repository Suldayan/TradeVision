package com.example.data_ingestion_service.services;

public interface DataAggregateService {
    void fetchDataAsync();
    <T> void sendDataToPartition(T data);
}
