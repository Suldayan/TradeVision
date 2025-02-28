package com.example.trade_vision_backend.ingestion.market.domain.mapper;

import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import com.example.trade_vision_backend.ingestion.market.infrastructure.model.RawMarketModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RawMarketMapper {
    RawMarketMapper INSTANCE = Mappers.getMapper(RawMarketMapper.class);

    RawMarketModel dtoToEntity(RawMarketDTO dto);
    Set<RawMarketModel> dtoSetToEntitySet(Set<RawMarketDTO> marketDTOS);
}
