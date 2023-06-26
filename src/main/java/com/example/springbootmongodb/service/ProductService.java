package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductRequest;

public interface ProductService {
    void create(ProductRequest request);
    Product findByName(String name);
}
