package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.model.ProductEntity;

import java.util.List;

public interface ProductService {
//    ProductEntity create(ProductRequest request);
    ProductEntity createAsync(ProductRequest request);
    ProductEntity updateAsync(String id, ProductRequest request);
    ProductEntity findById(String id);
    ProductEntity findByName(String name);
    boolean existsByName(String name);
    boolean existsById(String name);
    PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter);
    List<ProductEntity> searchProducts(String textSearch, Integer limit);
    void deleteById(String id);
}
