package com.example.data_ingestion_service.services.mapper;

import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.records.Exchange;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ExchangeMapper {
    ExchangeMapper INSTANCE = Mappers.getMapper(ExchangeMapper.class);

    Set<RawExchangesModel> exchangeRecordToEntity(Set<Exchange> exchangeRecords);
}
