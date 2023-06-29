package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.common.data.ProductVariation;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl extends DataBaseService<ProductEntity> implements ProductService {
    private final ProductRepository productRepository;
    private final ProductVariationService variationService;
    private final ProductMapper mapper;
    private final ProductItemService itemService;
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
      return createdProduct;
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
        return productRepository.existsById(name);    }
}
