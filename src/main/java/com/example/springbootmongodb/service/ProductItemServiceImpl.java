package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.common.data.mapper.ProductItemMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import io.jsonwebtoken.lang.Collections;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductItemServiceImpl implements ProductItemService {
    private final ProductItemRepository itemRepository;
    private final ProductItemMapper mapper;
    public static final String PRODUCT_MISSING_ITEMS_ERROR_MESSAGE = "Product is missing some items";
    public static final String NON_POSITIVE_QUANTITY_ERROR_MESSAGE = "Product sku must be equal or greater than 0";
    public static final String MINIMUM_PRICE_VIOLATION_ERROR_MESSAGE = "Product price must be equal or greater than 0";
    @Autowired
    @Lazy
    private ProductService productService;
    @Override
    @Transactional
    public List<ProductItemEntity> bulkCreate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations) {
        log.info("Performing ProductItemService bulkCreate");
        validateRequest(requests, variations);
        ProductEntity product = variations.get(0).getProduct();
        List<ProductItemEntity> newItems = new ArrayList<>();
        for (ProductItemRequest request : requests) {
            validateItemRequest(request);
            ProductItemEntity newItem = mapper.toEntity(request);
            List<VariationOptionEntity> newItemOptions = new ArrayList<>();
            for (int i = 0; i < request.getVariationIndex().size(); i++) {
                newItemOptions.add(variations.get(i).getOptions().get(request.getVariationIndex().get(i)));
            }
            newItem.getOptions().addAll(newItemOptions);
            newItem.setProduct(product);
            newItems.add(newItem);
        }
        return itemRepository.bulkCreate(newItems);
    }

    @Override
    public List<ProductItemEntity> bulkUpdate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations) {
        log.info("Performing ProductItemService bulkUpdate");
        validateRequest(requests, variations);
        ProductEntity product = variations.get(0).getProduct();
        if (requests.size() != product.getItems().size()) {
            throw new InvalidDataException(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE);
        }
        Map<String, ProductItemRequest> requestMap = new HashMap<>();
        for (ProductItemRequest request : requests) {
            validateItemRequest(request);
            requestMap.put(request.getId(), request);
        }
        for (ProductItemEntity updateItem : product.getItems()) {
            ProductItemRequest updateRequest = requestMap.get(updateItem.getId());
            if (updateRequest == null) {
                throw new InvalidDataException(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE);
            }
            mapper.updateFields(updateItem, updateRequest);
        }
        return itemRepository.bulkUpdate(product.getItems());
    }

    @Override
    public void bulkDisableByProductId(String productId) {
        log.info("Performing ProductItemService bulKDisableByProductId");
        productService.findById(productId);
        itemRepository.bulkDisableByProductId(productId);
    }

    @Override
    public void deleteByProductId(String productId) {
        log.info("Performing ProductItemService bulKDisableByProductId");
        if (StringUtils.isNotEmpty(productId)) {
            itemRepository.deleteByProductId(productId);
        }
    }

    private void validateRequest(List<ProductItemRequest> requests, List<ProductVariationEntity> variations) {
        if (Collections.isEmpty(requests)) {
            throw new InvalidDataException("Product items should not be empty");
        }
        if (Collections.isEmpty(variations)) {
            throw new InvalidDataException("Cannot save items with no variations");
        }
        int totalItems = 1;
        for (int i = 0; i < variations.size(); i++) {
            totalItems *= variations.get(i).getOptions().size();
        }
        if (requests.size() != totalItems) {
            throw new InvalidDataException(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE);
        }
    }

    private void validateItemRequest(ProductItemRequest request) {
        if (request.getQuantity() < 0) {
            throw new InvalidDataException(NON_POSITIVE_QUANTITY_ERROR_MESSAGE);
        }
        if (request.getPrice() <= 0) {
            throw new InvalidDataException(MINIMUM_PRICE_VIOLATION_ERROR_MESSAGE);
        }
    }
}
