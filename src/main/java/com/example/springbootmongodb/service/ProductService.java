package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.model.ProductEntity;

public interface ProductService {
    ProductEntity create(ProductRequest request);
    ProductEntity findById(String id);
    ProductEntity findByName(String name);
    boolean existsByName(String name);
    boolean existsById(String name);
}
