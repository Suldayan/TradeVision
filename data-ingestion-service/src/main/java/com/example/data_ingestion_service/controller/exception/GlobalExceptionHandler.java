package com.example.data_ingestion_service.controller.exception;

import com.example.data_ingestion_service.controller.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> dataNotFoundExceptionHandling(
            Exception exception,
            WebRequest request
    ) {
        return new ResponseEntity<>(new ErrorDetails(
                new Date(),
                exception.getMessage(),
                request.getDescription(false)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalException(
            Exception exception,
            WebRequest request
    ) {
        return new ResponseEntity<>(new ErrorDetails(
                new Date(),
                exception.getMessage(),
                request.getDescription(false)),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
