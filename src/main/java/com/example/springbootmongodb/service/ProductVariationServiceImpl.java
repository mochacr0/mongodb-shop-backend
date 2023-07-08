package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.BulkUpdateResult;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.data.VariationOptionRequest;
import com.example.springbootmongodb.common.data.mapper.ProductVariationMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    private final ThreadPoolTaskExecutor taskExecutor;
    public static final String DUPLICATED_VARIANT_NAME_ERROR_MESSAGE = "Cannot save variations with same name";
    public static final String NON_EXISTENT_PRODUCT_ERROR_MESSAGE = "Cannot refer to a non-existent product";
    public static final String REQUIRED_MINIMUM_VARIATIONS_ERROR_MESSAGE = "Product should have at least 1 variation";

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
    public List<ProductVariationEntity> bulkCreateAsync(List<ProductVariationRequest> requests, ProductEntity product) {
        log.info("Performing ProductVariationService bulkCreateAsync");
        validateRequest(requests, product);
        List<ProductVariationEntity> createdVariations = new ArrayList<>();
        List<CompletableFuture<ProductVariationEntity>> bulkCreateFutures = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ProductVariationEntity newVariation = createNewVariationData(requests.get(i), product, i);
            //create new single variation parallel
            bulkCreateFutures.add(createVariationAsync(requests.get(i), newVariation, product));
        }
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(bulkCreateFutures.toArray(new CompletableFuture[bulkCreateFutures.size()]));
        combinedFutures.join();
        createdVariations.addAll(bulkCreateFutures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        createdVariations.sort(new ProductVariationComparator());
        return createdVariations;
    }

    @Transactional
    public CompletableFuture<ProductVariationEntity> createVariationAsync(ProductVariationRequest request, ProductVariationEntity variation, ProductEntity product) {
        return CompletableFuture.supplyAsync(() -> {
            //TODO: upload images
            ProductVariationEntity createdVariation = super.insert(variation);
            List<VariationOptionEntity> createdOptions = optionService.bulkCreate(request.getOptions(), createdVariation);
            createdOptions.sort(new VariationOptionComparator());
            createdVariation.getOptions().addAll(createdOptions);
            return createdVariation;
        }, taskExecutor);
    }

    @Override
    @Transactional
    public BulkUpdateResult<ProductVariationEntity> bulkUpdateAsync(List<ProductVariationRequest> requests, ProductEntity product) {
        log.info("Performing ProductVariationService bulkUpdate");
        validateRequest(requests, product);
        List<ProductVariationEntity> savedVariations = new ArrayList<>();
        List<ProductVariationEntity> newVariations = new ArrayList<>();
        AtomicBoolean isModified = new AtomicBoolean(requests.size() != product.getVariations().size());
        List<CompletableFuture<ProductVariationEntity>> bulkOperationFutures = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ProductVariationRequest request = requests.get(i);
            ProductVariationEntity variation = findActiveVariation(request);
            if (variation != null && variation.getName().equals(request.getName()) && variation.getIndex() == i) {
//                CompletableFuture<Void> bulkUpdateFuture = CompletableFuture.supplyAsync(() -> {
//                    BulkUpdateResult<VariationOptionEntity> optionsUpdateResult = optionService.bulkUpdate(request.getOptions(), variation);
//                    List<VariationOptionEntity> savedOptions = optionsUpdateResult.getData();
//                    disabledOldOptions(variation.getOptions(), savedOptions);
//                    savedOptions.sort(new VariationOptionComparator());
//                    variation.setOptions(savedOptions);
//                    savedVariations.add(variation);
//                    isModified.set(isModified.get() || optionsUpdateResult.getIsModified().get());
//                    return null;
//                }, taskExecutor);
                CompletableFuture<ProductVariationEntity> bulkUpdateFuture = performBulkUpdateOptions(request.getOptions(), variation, isModified);
                bulkOperationFutures.add(bulkUpdateFuture);
            }
            else {
                isModified.set(true);
                newVariations.add(createNewVariationData(request, product, i));
            }
        }
        List<ProductVariationEntity> createdVariations = variationRepository.bulkCreate(newVariations);
        for (ProductVariationEntity createdVariation : createdVariations) {
            CompletableFuture<ProductVariationEntity> bulkCreateFuture  = performBulkCreateOptions(requests.get(createdVariation.getIndex()).getOptions(), createdVariation);
            bulkOperationFutures.add(bulkCreateFuture);
        }
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(bulkOperationFutures.toArray(new CompletableFuture[bulkOperationFutures.size()]));
        combinedFutures.join();
        savedVariations.addAll(bulkOperationFutures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
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

    @Override
    public void deleteByProductId(String productId) {
        log.info("Performing ProductVariationService deleteByProductId");
        if (StringUtils.isEmpty(productId)) {
            throw new InvalidDataException("Product Id should be specified");
        }
        variationRepository.deleteByProductId(productId);
    }

    @Override
    public ProductVariationEntity findById(String id) {
        log.info("Performing ProductVariationService findById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException("Proudct variation Id should be specified");
        }
        Optional<ProductVariationEntity> variationOpt = variationRepository.findById(id);
        if (variationOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Product variation with id [%s] is not found", id));
        }
        return variationOpt.get();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        log.info("Performing ProductVariationService deleteByiD");
        ProductVariationEntity variation = findById(id);
        optionService.deleteByVariationId(variation.getId());
        variationRepository.deleteById(id);
    }

    private void validateRequest(List<ProductVariationRequest> requests, ProductEntity product) {
        if (CollectionUtils.isEmpty(requests)) {
            throw new InvalidDataException(REQUIRED_MINIMUM_VARIATIONS_ERROR_MESSAGE);
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

    private CompletableFuture<ProductVariationEntity> performBulkUpdateOptions(List<VariationOptionRequest> requests, ProductVariationEntity variation, AtomicBoolean isModified) {
        return CompletableFuture.supplyAsync(() -> {
            BulkUpdateResult<VariationOptionEntity> optionsUpdateResult = optionService.bulkUpdate(requests, variation);
            List<VariationOptionEntity> savedOptions = optionsUpdateResult.getData();
            disabledOldOptions(variation.getOptions(), savedOptions);
            savedOptions.sort(new VariationOptionComparator());
            variation.setOptions(savedOptions);
            isModified.set(isModified.get() || optionsUpdateResult.getIsModified().get());
            return variation;
        }, taskExecutor);
    }

    private CompletableFuture<ProductVariationEntity> performBulkCreateOptions(List<VariationOptionRequest> requests, ProductVariationEntity createdVariation) {
        return CompletableFuture.supplyAsync(() -> {
            List<VariationOptionEntity> options = optionService.bulkCreate(requests, createdVariation);
            options.sort(new VariationOptionComparator());
            createdVariation.getOptions().addAll(options);
            return createdVariation;
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
