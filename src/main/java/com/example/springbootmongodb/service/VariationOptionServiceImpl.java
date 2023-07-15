package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.BulkUpdateResult;
import com.example.springbootmongodb.common.data.VariationOptionRequest;
import com.example.springbootmongodb.common.data.mapper.VariationOptionMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductVariationRepository;
import com.example.springbootmongodb.repository.VariationOptionRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariationOptionServiceImpl extends DataBaseService<VariationOptionEntity> implements VariationOptionService {
    private final VariationOptionRepository optionRepository;
    private final ProductVariationRepository variationRepository;
    private final VariationOptionMapper mapper;
    private final ThreadPoolTaskExecutor taskExecutor;

    public static final String DUPLICATED_OPTION_NAME_ERROR_MESSAGE = "Cannot save options with same name";
    public static final String NON_EXISTENT_VARIATION_ERROR_MESSAGE = "Cannot refer to a non-existent variation";
    public static final String REQUIRED_MINIMUM_OPTIONS_ERROR_MESSAGE = "Product variation should have at least 1 option";

    @Override
    public MongoRepository<VariationOptionEntity, String> getRepository() {
        return null;
    }
    @Override
    @Transactional
    public List<VariationOptionEntity> bulkCreate(List<VariationOptionRequest> requests, ProductVariationEntity variation) {
        log.info("Performing VariationOptionService bulkCreate");
        validateRequest(requests, variation);
        List<VariationOptionEntity> newOptions = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            newOptions.add(createNewOptionData(requests.get(i), variation, i));
        }
        List<VariationOptionEntity> createdOptions = optionRepository.bulkCreate(newOptions);
        createdOptions.sort(new VariationOptionComparator());
        return createdOptions;
    }

    @Override
    @Transactional
    public BulkUpdateResult<VariationOptionEntity> bulkUpdate(List<VariationOptionRequest> requests, ProductVariationEntity variation) {
        log.info("Performing VariationOptionService bulkUpdate");
        validateRequest(requests, variation);
        AtomicBoolean isModified = new AtomicBoolean(false);
        if (requests.size() != variation.getOptions().size()) {
            isModified.set(true);
        }
        List<VariationOptionEntity> updateOptions = new ArrayList<>();
        List<VariationOptionEntity> newOptions = new ArrayList<>();
        List<VariationOptionEntity> savedOptions = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            VariationOptionRequest request = requests.get(i);
            //TODO: Check to see if a product with this option is currently in the purchase flow. Return error
            VariationOptionEntity option = findActiveVariation(request);
            if (option != null && option.getName().equals(request.getName()) && option.getIndex() == i) {
                if (updateRequired(option, request)) {
                    option.setImageUrl(request.getImageUrl());
                    updateOptions.add(option);
                }
                else {
                    savedOptions.add(option);
                }
            }
            else {
                isModified.set(true);
                newOptions.add(createNewOptionData(request, variation, i));
            }
        }
        //bulk update saved options
        List<CompletableFuture<List<VariationOptionEntity>>> saveOptionsFutures = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updateOptions)) {
            saveOptionsFutures.add(CompletableFuture.supplyAsync(() -> optionRepository.bulkUpdate(updateOptions), taskExecutor));
        }
        if (CollectionUtils.isNotEmpty(newOptions)) {
            saveOptionsFutures.add(CompletableFuture.supplyAsync(() -> optionRepository.bulkCreate(newOptions), taskExecutor));
        }
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(saveOptionsFutures.toArray(new CompletableFuture[saveOptionsFutures.size()]));
        combinedFutures.join();
        for (CompletableFuture<List<VariationOptionEntity>> saveOptionsFuture : saveOptionsFutures) {
            savedOptions.addAll(saveOptionsFuture.join());
        }
        savedOptions.sort(new VariationOptionComparator());
        return new BulkUpdateResult<>(savedOptions, isModified);
    }

    @Override
    public void bulkDisable(List<VariationOptionEntity> disableOptions) {
        log.info("Performing VariationOptionService bulkDisable");
        if (!CollectionUtils.isEmpty(disableOptions)) {
            optionRepository.bulkDisable(disableOptions);
        }
    }

    @Override
    public void deleteByVariationId(String variationId) {
        log.info("Performing VariationOptionService deleteByVariationId");
        if (StringUtils.isNotEmpty(variationId)) {
            optionRepository.deleteByVariationId(variationId);
        }
    }

    private void validateRequest(List<VariationOptionRequest> requests, ProductVariationEntity variation) {
        if (CollectionUtils.isEmpty(requests)) {
            throw new InvalidDataException(REQUIRED_MINIMUM_OPTIONS_ERROR_MESSAGE);
        }
        if (containsDuplicates(requests, VariationOptionRequest::getName)) {
            throw new InvalidDataException(DUPLICATED_OPTION_NAME_ERROR_MESSAGE);
        }
        if (StringUtils.isEmpty(variation.getId())) {
            throw new UnprocessableContentException(NON_EXISTENT_VARIATION_ERROR_MESSAGE);
        }
    }

    private VariationOptionEntity createNewOptionData(VariationOptionRequest request, ProductVariationEntity variation, int index) {
        VariationOptionEntity newOption = mapper.toEntity(request);
        newOption.setVariation(variation);
        newOption.setIndex(index);
        return newOption;
    }

    private VariationOptionEntity findActiveVariation(VariationOptionRequest request) {
        if (StringUtils.isNotEmpty(request.getId())) {
            Optional<VariationOptionEntity> optionOpt = optionRepository.findById(request.getId());
            if (optionOpt.isEmpty() || optionOpt.get().isDisabled()) {
                 return null;
            }
            return optionOpt.get();
        }
        return null;
    }

    private boolean updateRequired(VariationOptionEntity entity, VariationOptionRequest request) {
        if (StringUtils.isEmpty(entity.getImageUrl()) && StringUtils.isEmpty(request.getImageUrl())) {
            return false;
        }
        return true;
    }

    public static class VariationOptionComparator implements Comparator<VariationOptionEntity> {
        @Override
        public int compare(VariationOptionEntity o1, VariationOptionEntity o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

}
