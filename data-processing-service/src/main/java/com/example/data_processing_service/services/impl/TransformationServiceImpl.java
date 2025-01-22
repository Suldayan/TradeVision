package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.mapper.AssetMapper;
import com.example.data_processing_service.models.mapper.ExchangeMapper;
import com.example.data_processing_service.models.processed.AssetModel;
import com.example.data_processing_service.models.processed.ExchangesModel;
import com.example.data_processing_service.models.processed.MarketModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import com.example.data_processing_service.repository.processed.AssetModelRepository;
import com.example.data_processing_service.repository.processed.ExchangeModelRepository;
import com.example.data_processing_service.repository.processed.MarketModelRepository;
import com.example.data_processing_service.services.FilterService;
import com.example.data_processing_service.services.TransformationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransformationServiceImpl implements TransformationService {
    private final MarketModelRepository marketModelRepository;
    private final ExchangeModelRepository exchangeModelRepository;
    private final AssetModelRepository assetModelRepository;

    private final ExchangeMapper exchangeMapper;
    private final AssetMapper assetMapper;

    private final FilterService filterService;

    @Nonnull
    @Override
    public Set<ExchangesModel> rawToEntityExchange(@Nonnull Set<String> ids) {
        return exchangeMapper.rawToProcessedExchanges(filterService.exchangeIdsToModels(ids));
    }

    @Nonnull
    @Override
    public Set<AssetModel> rawToEntityAsset(@Nonnull Set<String> ids) {
        return assetMapper.rawToProcessedAssetSet(filterService.assetIdsToModels(ids));
    }

    @Nonnull
    @Override
    public Map<String, ExchangesModel> indexExchangesById(@Nonnull Set<ExchangesModel> exchangesModels) {
        return exchangesModels.stream()
                .collect(Collectors.toMap(ExchangesModel::getExchangeId, Function.identity()));
    }

    @Nonnull
    @Override
    public Map<String, AssetModel> indexAssetsById(@Nonnull Set<AssetModel> assetModels) {
        return assetModels.stream()
                .collect(Collectors.toMap(AssetModel::getAssetId, Function.identity()));
    }

    /*
     * Completes market models by completing the exchange and asset relationship towards the market
     * Evicts the cache for the next scheduled fetch via the data aggregate service
     * */
    @CacheEvict(cacheNames = "marketApiResponse, exchangeApiResponse, assetApiResponse")
    @Override
    public void completeMarketAttributes() {
        Set<RawMarketModel> filteredMarketModels = filterService.filterMarkets();
        Map<String, ExchangesModel> exchanges = indexExchangesById();
        Map<String, AssetModel> assets = indexAssetsById();

        Set<MarketModel> marketModels = filteredMarketModels
                .stream()
                .map(attribute -> MarketModel.builder()
                        .exchange(
                                (ExchangesModel) exchanges.entrySet()
                                        .stream()
                                        .filter(exchangeModel -> Objects.equals(attribute.getExchangeId(), exchangeModel.getKey()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding exchange model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .baseAsset(
                                (AssetModel) assets.entrySet()
                                        .stream()
                                        .filter(baseModel -> Objects.equals(attribute.getBaseSymbol(), baseModel.getKey()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding base asset model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .quoteAsset(
                                (AssetModel) assets.entrySet()
                                        .stream()
                                        .filter(quoteModel -> Objects.equals(attribute.getBaseSymbol(), quoteModel.getKey()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding quote asset model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .priceUsd(attribute.getPriceUsd())
                        .updated(attribute.getUpdated())
                        .build())
                .collect(Collectors.toSet());
        saveToDatabase(marketModels);
    }

    /*
     * Generic function for saving to the database
     * @param takes a generic type T, entity, and will detect the class type of that entity and save it to its corresponding repository
     * */
    @Transactional
    @Override
    public <S> void saveToDatabase(@Nonnull Set<S> entities) {
        if (entities.isEmpty()) {
            log.warn("The list of entities has been passed but is empty");
            return;
        }
        // Grab the first element to find out what the data type is
        S firstElement = entities.iterator().next();
        switch (firstElement) {
            case MarketModel ignored -> {
                @SuppressWarnings(value = "unchecked")
                List<MarketModel> marketModels = (List<MarketModel>) entities;
                marketModelRepository.saveAll(marketModels);
                log.debug("Saving data of type: Market, with size: {}", entities.size());
            }
            case ExchangesModel ignored -> {
                @SuppressWarnings(value = "unchecked")
                List<ExchangesModel> exchangeModels = (List<ExchangesModel>) entities;
                exchangeModelRepository.saveAll(exchangeModels);
                log.debug("Saving data of type: Exchange, with size: {}", entities.size());
            }
            case AssetModel ignored -> {
                @SuppressWarnings(value = "unchecked")
                List<AssetModel> assetModels = (List<AssetModel>) entities;
                assetModelRepository.saveAll(assetModels);
                log.debug("Saving data of type: Asset, with size: {}", entities.size());
            }
            default -> log.warn("Info was sent to be saved but was not a recognizable type");
        }
    }
}
