package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.model.ProductEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductEntity create(ProductRequest request);
    ProductEntity update(String id, ProductRequest request);
    ProductEntity findById(String id);
    ProductEntity findByName(String name);
    boolean existsByName(String name);
    boolean existsById(String name);
    PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter);
    List<ProductEntity> searchProducts(String textSearch, Integer limit);
}
