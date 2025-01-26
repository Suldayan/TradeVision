package com.example.data_processing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DataProcessingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataProcessingServiceApplication.class, args);
	}

}
