package com.example.data_ingestion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableKafka
@EnableRetry
public class DataIngestionServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(DataIngestionServiceApplication.class, args);
	}

}
