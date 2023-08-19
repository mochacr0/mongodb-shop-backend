package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.ProductPageParameter;
import com.example.springbootmongodb.common.data.ProductPaginationResult;
import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.ProductEntity;

import java.util.List;
import java.util.Map;

public interface CustomProductRepository {
    PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter);
    List<ProductEntity> searchProducts(String textSearch, Integer limit);
    void updateTotalSales(Map<String, Integer> updateMap);

}
