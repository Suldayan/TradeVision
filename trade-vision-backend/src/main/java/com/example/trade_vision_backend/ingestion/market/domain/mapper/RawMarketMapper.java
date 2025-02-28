package com.example.trade_vision_backend.ingestion.market.domain.mapper;

import com.example.trade_vision_backend.ingestion.market.domain.dto.RawMarketDTO;
import com.example.trade_vision_backend.ingestion.market.infrastructure.model.RawMarketModel;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper
public interface RawMarketMapper {
    RawMarketModel dtoToEntity(RawMarketDTO dto);
    Set<RawMarketModel> dtoSetToEntitySet(Set<RawMarketDTO> marketDTOS);
}
