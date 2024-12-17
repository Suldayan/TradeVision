package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.services.exceptions.DataAggregateException;

public interface DataAggregateService {
    void fetchDataAsync() throws DataAggregateException;
    <T> void detectDataType(T data);
}
