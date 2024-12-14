package com.example.data_ingestion_service.record;

import java.math.BigDecimal;

/*
* A record to define thresholds for each variable within a data type
* @Param takes a string variable representing model variables and big decimal minChange for the threshold change
* */
public record DataThreshold(String variable, BigDecimal minChange) {
}
