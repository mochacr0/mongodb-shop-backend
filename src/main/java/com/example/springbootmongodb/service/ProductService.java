package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.ProductEntity;

import java.util.List;

public interface ProductService {
//    ProductEntity create(ProductRequest request);
    ProductEntity create(ProductRequest request);
    ProductEntity update(String productId, ProductRequest request);
    ProductEntity findById(String productId);
    ProductEntity findByName(String name);
    boolean existsByName(String name);
    boolean existsById(String name);
    PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter);
    List<ProductEntity> searchProducts(String textSearch, Integer limit);
    void deleteById(String productId);
    void updateTotalSales(List<OrderItem> orderItems);
    void updateRatings(String productId, double rating);
}
