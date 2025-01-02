package com.example.data_processing_service;

import org.springframework.boot.SpringApplication;

public class TestDataProcessingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(DataProcessingServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
