package com.example.data_ingestion_service.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDto {
    Date timestamp;
    Long batchTimestamp;
    String message;
    String details;
}
