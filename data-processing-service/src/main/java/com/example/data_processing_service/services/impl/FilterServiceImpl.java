package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.raw.RawAssetModel;
import com.example.data_processing_service.models.raw.RawExchangesModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import com.example.data_processing_service.repository.raw.RawAssetModelRepository;
import com.example.data_processing_service.repository.raw.RawExchangeModelRepository;
import com.example.data_processing_service.repository.raw.RawMarketModelRepository;
import com.example.data_processing_service.services.FilterService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {
    private final RawExchangeModelRepository rawExchangeModelRepository;
    private final RawAssetModelRepository rawAssetModelRepository;
    private final RawMarketModelRepository rawMarketModelRepository;

    /*
     * Creates a new list of raw market models containing only markets with meaningful price changes
     * @return a list of market transfer objects that contain these changes
     * */
    //TODO we can add a timestamp param to these functions and it will be grabbed via kafka consumer and provided via composeAsync service
    @Nonnull
    @Override
    public Set<RawMarketModel> filterMarkets() {
        Set<RawMarketModel> rawMarketData = rawMarketModelRepository.findAllByTimestamp();
        return rawMarketData.stream()
                .filter(this::isPriceChangeMeaningful)
                .map(data -> RawMarketModel.builder()
                        .id(data.getId())
                        .rank(data.getRank())
                        .priceQuote(data.getPriceQuote())
                        .priceUsd(data.getPriceUsd())
                        .volumeUsd24Hr(data.getVolumeUsd24Hr())
                        .percentExchangeVolume(data.getPercentExchangeVolume())
                        .tradesCount(data.getTradesCount())
                        .updated(data.getUpdated())
                        .exchangeId(data.getExchangeId())
                        .quoteId(data.getQuoteId())
                        .baseSymbol(data.getBaseSymbol())
                        .quoteSymbol(data.getQuoteSymbol())
                        .build())
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<String> filterExchangeIds(@Nonnull Set<RawMarketModel> filteredMarketModels) {
        return filteredMarketModels.stream()
                .map(RawMarketModel::getExchangeId)
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<String> filterAssetIds(@Nonnull Set<RawMarketModel> filteredMarketModels) {
        Set<String> baseIds = filteredMarketModels.stream()
                .map(RawMarketModel::getId)
                .collect(Collectors.toSet());
        Set<String> quoteIds = filteredMarketModels.stream()
                .map(RawMarketModel::getQuoteId)
                .collect(Collectors.toSet());
        baseIds.addAll(quoteIds);
        return baseIds;
    }

    @Nonnull
    @Override
    public Set<RawExchangesModel> exchangeIdsToModels(@Nonnull Set<String> exchangeIds) {
        //TODO from here, we might have to first index by timestamp, then findByID. a few flows to go but this is one of them
        // Think this more through, lowkey tired but now that i think about it, this todo is not needed lmao
        Set<RawExchangesModel> unfilteredModels = rawExchangeModelRepository.findAllByTimestamp();
        return unfilteredModels.stream()
                .filter(model -> exchangeIds.contains(model.getId()))
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<RawAssetModel> assetIdsToModels(@Nonnull Set<String> assetIds) {
        Set<RawAssetModel> unfilteredAssets = rawAssetModelRepository.findAllByTimestamp();
        return unfilteredAssets.stream()
                .filter(model -> assetIds.contains(model.getId()))
                .collect(Collectors.toSet());
    }

    /*
     * Determines whether the price change is meaningful enough to be saved into the database unless it does not already exist within it
     * @param takes a type MarketDTO to be used for price comparison
     * @return a boolean if the price change is more than or equal to 5%
     * */
    @Override
    public Boolean isPriceChangeMeaningful(@Nonnull RawMarketModel cachedData) {
        // TODO change this for fetching via previous timestamps
        // If done correctly, then subtracting 5 minutes from the current timestamp should retrieve the last fetched data
        // We should think about a proper check on whether to compare to a successful timestamp as apposed to a failed one
        // For example, if 12345L isn't meaningful compared to 13456L but it is to 15678L, then that could deem valid and we move the comparison timestamp to this one
        Optional<RawMarketModel> lastSignificantChange = rawMarketModelRepository.findById(cachedData.getId());
        if (lastSignificantChange.isEmpty()) {
            return true;
        }
        // Get the last significant price
        BigDecimal lastPrice = lastSignificantChange.get().getPriceUsd();
        if (lastPrice.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        // Calculate percentage change
        BigDecimal currentPrice = cachedData.getPriceUsd();
        BigDecimal percentageChange = currentPrice
                .subtract(lastPrice)
                .abs()
                .divide(lastPrice, MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100));
        // Check if the change is >= 5%
        return percentageChange.compareTo(BigDecimal.valueOf(5)) >= 0;
    }
}
