package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.common.validator.CommonValidator;
import com.example.springbootmongodb.exception.IncorrectParameterException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.repository.ProductRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
    public class ProductServiceImpl extends DataBaseService<ProductEntity> implements ProductService {
    private final ProductRepository productRepository;
    private final ProductVariationService variationService;
    private final ProductMapper mapper;
    private final ProductItemService itemService;
    private final CommonValidator commonValidator;
    public static final String DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE = "There is already a product with that name";
    @Override
    public MongoRepository<ProductEntity, String> getRepository() {
        return productRepository;
    }
    @Override
    @Transactional
    public ProductEntity create(ProductRequest request) {
      log.info("Performing ProductService create");
      //TODO: validate non-existent category id
      if (existsByName(request.getName())) {
          throw new InvalidDataException(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE);
      }
      ProductEntity createdProduct = super.insert(mapper.toEntity(request));
      if (Collections.isEmpty(request.getVariations())) {
          return createdProduct;
      }
      List<ProductVariationEntity> createdVariations = variationService.bulkCreate(request.getVariations(), createdProduct);
      List<ProductItemEntity> createdItems = itemService.bulkCreate(request.getItems(), createdVariations);
      createdProduct.setVariations(createdVariations);
      createdProduct.setItems(createdItems);
      updatePriceRange(createdProduct);
      return super.save(createdProduct);
    }

    @Override
    @Transactional
    public ProductEntity update(String id, ProductRequest request) {
        log.info("Performing ProductService update");
        ProductEntity existingProduct = findById(id);
        if (!existingProduct.getName().equals(request.getName())) {
            Optional<ProductEntity> nameDuplicatedProductOpt = productRepository.findByName(request.getName());
            if (nameDuplicatedProductOpt.isPresent() && !existingProduct.getId().equals(nameDuplicatedProductOpt.get().getId())) {
                throw new InvalidDataException(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE);
            }
        }
        mapper.updateFields(existingProduct, request);
        List<ProductVariationEntity> oldVariations = existingProduct.getVariations();
        BulkUpdateResult<ProductVariationEntity> updateVariationsResult = variationService.bulkUpdate(request.getVariations(), existingProduct);
        List<ProductVariationEntity> updatedVariations = updateVariationsResult.getData();
        //disable old variations
        disabledOldVariations(oldVariations, updatedVariations);
        List<ProductItemEntity> savedItems;
        if (updateVariationsResult.getIsModified().get()) {
            itemService.bulkDisableByProductId(existingProduct.getId());
            savedItems = itemService.bulkCreate(request.getItems(), updatedVariations);
        }
        else {
            //create new items
            savedItems = itemService.bulkUpdate(request.getItems(), updatedVariations);
        }
        existingProduct.setVariations(updatedVariations);
        existingProduct.setItems(savedItems);
        updatePriceRange(existingProduct);
        return super.save(existingProduct);
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
            float minPrice = Float.MAX_VALUE;
            float maxPrice = Float.MIN_VALUE;
            for (ProductItemEntity item : items) {
                float price = item.getPrice();
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
