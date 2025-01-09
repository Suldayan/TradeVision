package com.example.data_ingestion_service.services.mapper;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface MarketMapper {
    MarketMapper INSTANCE = Mappers.getMapper(MarketMapper.class);

    Set<RawMarketModel> marketRecordToEntity(Set<Market> marketRecords);
}
