package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.common.data.ProductVariation;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl extends DataBaseService<ProductEntity> implements ProductService {
    private final ProductRepository productRepository;
    private final ProductVariationService variationService;
    public static final String DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE = "There is already a product with that name";
    @Override
    public MongoRepository<ProductEntity, String> getRepository() {
        return productRepository;
    }
    @Override
    public void create(ProductRequest request) {
      log.info("Performing ProductService create");
      Product nameDuplicatedProduct = this.findByName(request.getName());
      if (nameDuplicatedProduct != null) {
          throw new InvalidDataException(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE);
      }
      ProductEntity createdProduct = super.insert(request.toEntity());
      List<ProductVariation> createdVariations = variationService.bulkCreate(request.getVariations(), createdProduct.getId());
      //create variation options
      //create product item
    }

    @Override
    public Product findByName(String name) {
        log.info("Performing ProductService findByName");
        return DaoUtils.toData(productRepository.findByName(name), Product::fromEntity);
    }
}
