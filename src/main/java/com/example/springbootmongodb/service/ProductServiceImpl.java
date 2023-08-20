package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.common.validator.CommonValidator;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.springbootmongodb.common.validator.ConstraintValidator.validateFields;

@Service
@Slf4j
@RequiredArgsConstructor
    public class ProductServiceImpl extends DataBaseService<ProductEntity> implements ProductService {
    private final ProductRepository productRepository;
    private final ProductVariationService variationService;
    private final ProductMapper mapper;
    private final ProductItemService itemService;
    private final CommonValidator commonValidator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final MediaService mediaService;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public static final String DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE = "There is already a product with that name";
    public static final String MINIMUM_WEIGHT_VIOLATION_ERROR_MESSAGE = "Product weight must be greater than 0";
    public static final String REQUIRED_PRODUCT_NAME_ERROR_MESSAGE = "Product name should be specified";

    @Override
    public MongoRepository<ProductEntity, String> getRepository() {
        return productRepository;
    }


    @Override
    @Transactional
    public ProductEntity create(ProductRequest request) {
        log.info("Performing ProductService create");
        validateRequest(request);
        if (existsByName(request.getName())) {
            throw new InvalidDataException(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE);
        }
        if (StringUtils.isEmpty(request.getCategoryId())) {
            CategoryEntity defaultCategory = categoryService.findDefaultCategory();
            if (defaultCategory != null) {
                request.setCategoryId(defaultCategory.getId());
            }
        }
        else {
            try {
                categoryService.findById(request.getCategoryId());
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException(exception.getMessage());
            }
        }
        ProductEntity newProduct = mapper.toEntity(request);
        ProductEntity createdProduct = super.insert(newProduct);
        List<ProductVariationEntity> createdVariations = variationService.bulkCreateAsync(request.getVariations(), createdProduct);
        List<ProductItemEntity> createdItems = itemService.bulkCreate(request.getItems(), createdVariations, request.getWeight());
        createdProduct.setVariations(createdVariations);
        createdProduct.setItems(createdItems);
        updatePriceRange(createdProduct);
        super.save(createdProduct);
        mediaService.persistCreatingProductImages(request);
        return createdProduct;
    }

    private void validateRequest(ProductRequest request) {
        validateFields(request);
        if (request.getWeight() <= 0) {
            throw new InvalidDataException(MINIMUM_WEIGHT_VIOLATION_ERROR_MESSAGE);
        }
    }


    @Override
    @Transactional
    public ProductEntity update(String id, ProductRequest request) {
        log.info("Performing ProductService updateAsync");
        validateRequest(request);
        ProductEntity existingProduct = findById(id);
        if (!existingProduct.getName().equals(request.getName())) {
            Optional<ProductEntity> nameDuplicatedProductOpt = productRepository.findByName(request.getName());
            if (nameDuplicatedProductOpt.isPresent() && !existingProduct.getId().equals(nameDuplicatedProductOpt.get().getId())) {
                throw new InvalidDataException(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE);
            }
        }
        CategoryEntity category = null;
        try {
            if (StringUtils.isEmpty(request.getCategoryId())) {
                category = categoryService.findDefaultCategory();
            }
            else if (!request.getCategoryId().equals(existingProduct.getCategoryId())) {
                category = categoryService.findById(request.getCategoryId());
            }
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        if (category != null) {
            request.setCategoryId(category.getId());
        }
        ProductEntity oldProduct;
        try {
            oldProduct = objectMapper.readValue(objectMapper.writeValueAsString(existingProduct), ProductEntity.class);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Internal Server Error, please try again");
        }
        mapper.updateFields(existingProduct, request);
        List<ProductVariationEntity> oldVariations = existingProduct.getVariations();
        BulkUpdateResult<ProductVariationEntity> updateVariationsResult = variationService.bulkUpdateAsync(request.getVariations(), existingProduct);
        List<ProductVariationEntity> updatedVariations = updateVariationsResult.getData();
        //disable old variations
        disabledOldVariations(oldVariations, updatedVariations);
        List<ProductItemEntity> savedItems;
        if (updateVariationsResult.getIsModified().get()) {
            itemService.bulkDisableByProductId(existingProduct.getId());
            savedItems = itemService.bulkCreate(request.getItems(), updatedVariations, existingProduct.getWeight());
        }
        else {
            //create new items
            savedItems = itemService.bulkUpdate(request.getItems(), updatedVariations, existingProduct.getWeight());
        }
        existingProduct.setVariations(updatedVariations);
        existingProduct.setItems(savedItems);
        updatePriceRange(existingProduct);
        mediaService.persistUpdatingProductImages(request, oldProduct);
        super.save(existingProduct);
        return existingProduct;
    }

    @Override
    public ProductEntity findById(String id) {
        log.info("Performing ProductService findById");
        Optional<ProductEntity> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Product with id [%s] is not found", id));
        }
        return productOpt.get();
    }

    @Override
    public ProductEntity findByName(String name) {
        log.info("Performing ProductService findByName");
        if (StringUtils.isEmpty(name)) {
            throw new InvalidDataException(REQUIRED_PRODUCT_NAME_ERROR_MESSAGE);
        }
        Optional<ProductEntity> productOpt = productRepository.findByName(name);
        if (productOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Product with name [%s] is not found", name));
        }
        return productOpt.get();
    }

    @Override
    public boolean existsByName(String name) {
        log.info("Performing ProductService existsByName");
        return productRepository.existsByName(name);
    }

    @Override
    public boolean existsById(String name) {
        log.info("Performing ProductService existsByName");
        return productRepository.existsById(name);
    }

    @Override
    public PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter) {
        log.info("Performing ProductService findProducts");
        commonValidator.validatePageParameter(pageParameter);
        return productRepository.findProducts(pageParameter);
    }

    @Override
    public List<ProductEntity> searchProducts(String textSearch, Integer limit) {
        log.info("Performing ProductService searchProducts");
        return productRepository.searchProducts(textSearch, limit);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        log.info("Performing ProductService deleteById");
        ProductEntity product = findById(id);
        List<CompletableFuture<Void>> deleteVariationsFuture = null;
        if (CollectionUtils.isNotEmpty(product.getVariations())) {
            deleteVariationsFuture = product.getVariations().stream().map(variation -> {
                CompletableFuture<Void> bulkCreateFuture = CompletableFuture.supplyAsync(() -> {
                    variationService.deleteById(variation.getId());
                    return null;
                }, taskExecutor);
                return bulkCreateFuture;
            }).collect(Collectors.toList());
        }
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(deleteVariationsFuture.toArray(new CompletableFuture[deleteVariationsFuture.size()]));
        combinedFuture.join();
        itemService.deleteByProductId(product.getId());
        productRepository.deleteById(product.getId());
    }

    @Override
    public void updateTotalSales(List<OrderItem> orderItems) {
        log.info("Performing ProductService updateTotalSales");
        Map<String, Integer> updateMap = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            String productId = orderItem.getProductId();
            if (!updateMap.containsKey(productId)) {
                updateMap.put(productId, orderItem.getQuantity());
            }
            else {
                updateMap.put(productId, updateMap.get(productId) + orderItem.getQuantity());
            }
        }
        productRepository.updateTotalSales(updateMap);

    }

    @Override
    public void updateRatings(String productId, double rating) {
        log.info("Performing ProductService updateRating");
        ProductEntity product;
        try {
            product = findById(productId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        product.setRating(reviewService.calculateProductRatings(product.getId()));
        save(product);
    }

    private void disabledOldVariations(List<ProductVariationEntity> oldVariations, List<ProductVariationEntity> updatedVariations) {
        Set<String> updatedOptionIds = updatedVariations.stream().map(ProductVariationEntity::getId).collect(Collectors.toSet());
        List<ProductVariationEntity> disableOptions = oldVariations.stream().filter(variation -> !updatedOptionIds.contains(variation.getId())).toList();
        //bulk disable variations
        if (CollectionUtils.isNotEmpty(disableOptions)) {
            variationService.bulkDisable(disableOptions);
        }
    }

    private void updatePriceRange(ProductEntity product) {
        List<ProductItemEntity> items = product.getItems();
        if (items != null && !items.isEmpty()) {
            long minPrice = Long.MAX_VALUE;
            long maxPrice = Long.MIN_VALUE;
            for (ProductItemEntity item : items) {
                long price = item.getPrice();
                if (price < minPrice) {
                    minPrice = price;
                }
                if (price > maxPrice) {
                    maxPrice = price;
                }
            }
            product.setMinPrice(minPrice);
            product.setMaxPrice(maxPrice);
        }
    }
}
