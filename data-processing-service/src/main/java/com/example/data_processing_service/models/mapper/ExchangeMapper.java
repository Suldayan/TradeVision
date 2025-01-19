package com.example.data_processing_service.models.mapper;

import com.example.data_processing_service.models.processed.ExchangesModel;
import com.example.data_processing_service.models.raw.RawExchangesModel;
import jakarta.annotation.Nonnull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExchangeMapper {
    ExchangeMapper INSTANCE = Mappers.getMapper(ExchangeMapper.class);

    ExchangesModel rawToProcessedExchanges(@Nonnull RawExchangesModel rawExchangesModel);
}
