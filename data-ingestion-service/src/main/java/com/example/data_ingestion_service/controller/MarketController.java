package com.example.data_ingestion_service.controller;

import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data-ingestion")
@Slf4j
@RequiredArgsConstructor
public class MarketController {
    private final RawMarketModelRepository marketModelRepository;
}
