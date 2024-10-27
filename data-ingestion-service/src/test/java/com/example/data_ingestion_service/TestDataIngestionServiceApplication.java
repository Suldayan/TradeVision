package com.example.data_ingestion_service;

import org.springframework.boot.SpringApplication;

public class TestDataIngestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(DataIngestionServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
