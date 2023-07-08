package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.ProductPageParameter;
import com.example.springbootmongodb.common.data.ProductPaginationResult;
import com.example.springbootmongodb.model.ProductEntity;

import java.util.List;

public interface CustomProductRepository {
    PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter);
    List<ProductEntity> searchProducts(String textSearch, Integer limit);

}
