package com.example.data_ingestion_service.service;

import com.example.data_ingestion_service.client.ApiClient;
import com.example.data_ingestion_service.exception.CustomApiServiceException;
import com.example.data_ingestion_service.model.RawMarketWrapperModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ApiService {

    private final ApiClient apiClient;

    public ApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /*
    * Periodically fetches market data from the coin cap api
    * @returns a response entity of object RawMarketWrapperModel, where the list of
    * rawMarketModels represents the actual data being saved in the database
    * */
    //TODO: wrap with retry -> circuit breaking -> scheduled
    public ResponseEntity<RawMarketWrapperModel> fetchMarketData() throws CustomApiServiceException{
        try {
            log.info("Fetching market data");
            log.debug("Fetching market data at: {}", LocalDateTime.now());
            return apiClient.getAllMarketData();
        } catch (Exception error) {
            log.error("Failed to fetch market data: {}", error.getMessage());
            String errorMessage = String.format(
                    "Error while fetching market data at: %s, %s",
                    LocalDateTime.now(), error.getMessage());
            throw new CustomApiServiceException(errorMessage);
        }
    }
}
