package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.common.data.mapper.ProductItemMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import io.jsonwebtoken.lang.Collections;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductItemServiceImpl extends DataBaseService<ProductItemEntity> implements ProductItemService {
    private final ProductItemRepository itemRepository;
    private final ProductItemMapper mapper;
    public static final String PRODUCT_MISSING_ITEMS_ERROR_MESSAGE = "Product is missing some items";
    public static final String NON_POSITIVE_QUANTITY_ERROR_MESSAGE = "Product quantity must be equal or greater than 0";
    public static final String MINIMUM_PRICE_VIOLATION_ERROR_MESSAGE = "Product price must be equal or greater than 0";
    public static final String REQUIRED_ITEM_ID_ERROR_MESSAGE = "Product item Id should be specified";
    @Autowired
    @Lazy
    private ProductService productService;

    @Override
    public MongoRepository<ProductItemEntity, String> getRepository() {
        return itemRepository;
    }

    @Override
    @Transactional
    public List<ProductItemEntity> bulkCreate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations, double productWeight) {
        log.info("Performing ProductItemService bulkCreate");
        validateRequest(requests, variations);
        ProductEntity product = variations.get(0).getProduct();
        List<ProductItemEntity> newItems = new ArrayList<>();
        for (ProductItemRequest request : requests) {
            request.setWeight(productWeight);
            validateItemRequest(request);
            ProductItemEntity newItem = mapper.toEntity(request);
            String variationIndex = request.getVariationIndex().stream().map(String::valueOf).collect(Collectors.joining(","));
            List<VariationOptionEntity> newItemOptions = new ArrayList<>();
            String variationDescription = "";
            String imageUrl = product.getImageUrl();
            for (int i = 0; i < request.getVariationIndex().size(); i++) {
                //get option
                ProductVariationEntity newItemVariation = variations.get(i);
                VariationOptionEntity newItemOption = newItemVariation.getOptions().get(request.getVariationIndex().get(i));
                //get image url from option
                if (StringUtils.isNotEmpty(newItemOption.getImageUrl())) {
                    imageUrl = newItemOption.getImageUrl();
                }
                //add this option into item's option list
                newItemOptions.add(newItemOption);
                //format variation description
                variationDescription = variationDescription.concat(String.format("%s:%s, ", newItemVariation.getName(), newItemOption.getName()));
            }
            //remove the last comma
            variationDescription = variationDescription.substring(0, variationDescription.length() - 2);
            newItem.getOptions().addAll(newItemOptions);
            newItem.setProduct(product);
            newItem.setVariationDescription(variationDescription);
            newItem.setVariationIndex(variationIndex);
            newItem.setImageUrl(imageUrl);
            newItems.add(newItem);
        }
        return itemRepository.bulkCreate(newItems);
    }

    @Override
    public List<ProductItemEntity> bulkUpdate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations, double productWeight) {
        log.info("Performing ProductItemService bulkUpdate");
        validateRequest(requests, variations);
        ProductEntity product = variations.get(0).getProduct();
        if (requests.size() != product.getItems().size()) {
            throw new InvalidDataException(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE);
        }
        Map<String, ProductItemRequest> requestMap = new HashMap<>();
        for (ProductItemRequest request : requests) {
            request.setWeight(productWeight);
            validateItemRequest(request);
            requestMap.put(request.getId(), request);
        }
        for (ProductItemEntity updateItem : product.getItems()) {
            ProductItemRequest updateRequest = requestMap.get(updateItem.getId());
            if (updateRequest == null) {
                throw new InvalidDataException(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE);
            }
            String imageUrl = getImageUrl(variations, product, updateRequest);
            mapper.updateFields(updateItem, updateRequest);
            updateItem.setImageUrl(imageUrl);
        }
        return itemRepository.bulkUpdate(product.getItems());
    }

    private String getImageUrl(List<ProductVariationEntity> variations, ProductEntity product, ProductItemRequest updateRequest) {
        String imageUrl = product.getImageUrl();
        VariationOptionEntity primaryOption = variations.get(0).getOptions().get(updateRequest.getVariationIndex().get(0));
        if (StringUtils.isNotEmpty(primaryOption.getImageUrl())) {
            imageUrl = primaryOption.getImageUrl();
        }
        return imageUrl;
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

    @Override
    public ProductItemEntity findById(String id) {
        log.info("Performing ProductItemService findById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException(REQUIRED_ITEM_ID_ERROR_MESSAGE);
        }
        return itemRepository.findById(id).orElseThrow(() ->
                new ItemNotFoundException(String.format("Product item with Id [%s] is not found", id)));
    }


    @Override
    public ProductItemEntity save(ProductItemEntity productItem) {
        log.info("Performing ProductItemService save");
        return super.save(productItem);
    }
}
