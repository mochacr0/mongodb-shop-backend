package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.BulkUpdateResult;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.data.mapper.ProductVariationMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.ProductVariationRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductVariationServiceImpl extends DataBaseService<ProductVariationEntity> implements ProductVariationService {
    private final ProductVariationRepository variationRepository;
    private final ProductVariationMapper variationMapper;
    private final ProductRepository productRepository;
    private final VariationOptionService optionService;
    private final ThreadPoolTaskScheduler taskExecutor;
    public static final String DUPLICATED_VARIANT_NAME_ERROR_MESSAGE = "Cannot save variations with same name";
    public static final String NON_EXISTENT_PRODUCT_ERROR_MESSAGE = "Cannot refer to a non-existent product";

    @Override
    public MongoRepository<ProductVariationEntity, String> getRepository() {
        return variationRepository;
    }

//    @Override
//    public ProductVariationEntity findById(String id) {
//        log.info("Performing ProductVariationService bulkCreate");
//        if (StringUtils.isEmpty(id)) {
//            throw new InvalidDataException("Variation id should be specified");
//        }
//        Optional<ProductVariationEntity> variationOpt = variationRepository.findById(id);
//        if (variationOpt.isEmpty()) {
//            throw new ItemNotFoundException(String.format("Variation with id [%s] is not found", id));
//        }
//        return variationOpt.get();
//    }

    @Override
    @Transactional
    public List<ProductVariationEntity> bulkCreate(List<ProductVariationRequest> requests, ProductEntity product) {
        log.info("Performing ProductVariationService bulkCreate");
        validateRequest(requests, product);
        List<ProductVariationEntity> newVariations = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            newVariations.add(createNewVariationData(requests.get(i), product, i));
        }
        List<ProductVariationEntity> createdVariations = variationRepository.bulkCreate(newVariations);
        createdVariations.sort(new ProductVariationComparator());
        List<CompletableFuture<Void>> bulkCreateFutures = new ArrayList<>();
//        for (int i = 0; i < requests.size(); i++) {
//            List<VariationOptionEntity> options = optionService.bulkCreate(requests.get(i).getOptions(), createdVariations.get(i));
//            options.sort(new VariationOptionComparator());
//            createdVariations.get(i).getOptions().addAll(options);
//        }
        for (int i = 0; i < requests.size(); i++) {
            int finalI = i;
            CompletableFuture<Void> bulkCreateFuture = CompletableFuture.supplyAsync(() -> {
                log.info("Future running");
                List<VariationOptionEntity> options = optionService.bulkCreate(requests.get(finalI).getOptions(), createdVariations.get(finalI));
                options.sort(new VariationOptionComparator());
                createdVariations.get(finalI).getOptions().addAll(options);
                return null;
            }, taskExecutor);
            bulkCreateFutures.add(bulkCreateFuture);
        }
        log.info("Log running");
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(bulkCreateFutures.toArray(new CompletableFuture[bulkCreateFutures.size()]));
        combinedFutures.join();
        return createdVariations;
    }

    @Override
    @Transactional
    public BulkUpdateResult<ProductVariationEntity> bulkUpdate(List<ProductVariationRequest> requests, ProductEntity product) {
        log.info("Performing ProductVariationService bulkUpdate");
        AtomicBoolean isModified = new AtomicBoolean(false);
        validateRequest(requests, product);
        List<ProductVariationEntity> savedVariations = new ArrayList<>();
        List<ProductVariationEntity> newVariations = new ArrayList<>();
        if (requests.size() != product.getVariations().size()) {
            isModified.set(true);
        }
        List<CompletableFuture<Void>> bulkOperationFutures = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ProductVariationRequest request = requests.get(i);
            ProductVariationEntity variation = findActiveVariation(request);
            if (variation != null && variation.getName().equals(request.getName()) && variation.getIndex() == i) {
                CompletableFuture<Void> bulkUpdateFuture = performVariationUpdate(requests.get(i), variation, isModified);
                bulkOperationFutures.add(bulkUpdateFuture);
            }
            else {
                isModified.set(true);
                newVariations.add(createNewVariationData(request, product, i));
            }
        }
        List<ProductVariationEntity> createdVariations = variationRepository.bulkCreate(newVariations);
        for (ProductVariationEntity createdVariation : createdVariations) {
            CompletableFuture<Void> bulkCreateFuture = performVariationCreation(requests, createdVariation, savedVariations);
            bulkOperationFutures.add(bulkCreateFuture);
        }
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(bulkOperationFutures.toArray(new CompletableFuture[bulkOperationFutures.size()]));
        combinedFutures.join();
        savedVariations.sort(new ProductVariationComparator());
        return new BulkUpdateResult<>(savedVariations, isModified);
    }

    @Override
    public void bulkDisable(List<ProductVariationEntity> disableVariations) {
        log.info("Performing ProductVariationService bulkDisable");
        if (!CollectionUtils.isEmpty(disableVariations)) {
            variationRepository.bulkDisable(disableVariations);

        }
    }

    private void validateRequest(List<ProductVariationRequest> requests, ProductEntity product) {
        if (CollectionUtils.isEmpty(requests)) {
            throw new InvalidDataException("Product should have at least 1 variation");
        }
        if (containsDuplicates(requests, ProductVariationRequest::getName)) {
            throw new InvalidDataException(DUPLICATED_VARIANT_NAME_ERROR_MESSAGE);
        }
        if (!productRepository.existsById(product.getId())) {
            throw new UnprocessableContentException(NON_EXISTENT_PRODUCT_ERROR_MESSAGE);
        }
    }

    private ProductVariationEntity createNewVariationData(ProductVariationRequest request, ProductEntity product, int index) {
        ProductVariationEntity newVariation = variationMapper.toEntity(request);
        newVariation.setProduct(product);
        newVariation.setIndex(index);
        return newVariation;
    }

    private ProductVariationEntity findActiveVariation(ProductVariationRequest request) {
        if (StringUtils.isNotEmpty(request.getId())) {
            Optional<ProductVariationEntity> variationOpt =  variationRepository.findById(request.getId());
            if (variationOpt.isEmpty() || variationOpt.get().isDisabled()) {
                return null;
            }
            return variationOpt.get();
        }
        return null;
    }

    private void disabledOldOptions(List<VariationOptionEntity> oldOptions, List<VariationOptionEntity> updatedOptions) {
        Set<String> updatedOptionIds = updatedOptions.stream().map(VariationOptionEntity::getId).collect(Collectors.toSet());
        List<VariationOptionEntity> disableOptions = oldOptions.stream().filter(option -> !updatedOptionIds.contains(option.getId())).toList();
        //bulk disable options
        if (CollectionUtils.isNotEmpty(disableOptions)) {
            optionService.bulkDisable(disableOptions);
        }
    }

    private CompletableFuture<Void> performVariationUpdate(ProductVariationRequest request, ProductVariationEntity variation, AtomicBoolean isModified) {
        return CompletableFuture.supplyAsync(() -> {
            BulkUpdateResult<VariationOptionEntity> optionsUpdateResult = optionService.bulkUpdate(request.getOptions(), variation);
            List<VariationOptionEntity> savedOptions = optionsUpdateResult.getData();
            disabledOldOptions(variation.getOptions(), savedOptions);
            savedOptions.sort(new VariationOptionComparator());
            variation.setOptions(savedOptions);
            isModified.set(isModified.get() || optionsUpdateResult.getIsModified().get());
            return null;
        }, taskExecutor);
    }

    private CompletableFuture<Void> performVariationCreation(List<ProductVariationRequest> requests, ProductVariationEntity createdVariation, List<ProductVariationEntity> savedVariations) {
        int index = createdVariation.getIndex();
        return CompletableFuture.supplyAsync(() -> {
            List<VariationOptionEntity> options = optionService.bulkCreate(requests.get(index).getOptions(), createdVariation);
            options.sort(new VariationOptionComparator());
            createdVariation.getOptions().addAll(options);
            savedVariations.add(createdVariation);
            return null;
        }, taskExecutor);
    }

    static class ProductVariationComparator implements Comparator<ProductVariationEntity> {
        @Override
        public int compare(ProductVariationEntity o1, ProductVariationEntity o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

    static class VariationOptionComparator implements Comparator<VariationOptionEntity> {
        @Override
        public int compare(VariationOptionEntity o1, VariationOptionEntity o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

}
