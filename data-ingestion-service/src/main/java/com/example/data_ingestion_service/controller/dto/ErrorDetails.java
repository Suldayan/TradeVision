package com.example.data_ingestion_service.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDetails {
    Date timestamp;
    String message;
    String details;
}
