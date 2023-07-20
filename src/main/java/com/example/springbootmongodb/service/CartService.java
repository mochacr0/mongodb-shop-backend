package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.UpdateCartItemRequest;
import com.example.springbootmongodb.model.CartEntity;
import com.example.springbootmongodb.model.CartItemEntity;

import java.util.List;

public interface CartService {
    CartEntity create(String userId);
    CartEntity findByUserId(String userId);
    boolean existsByUserId(String userId);
    void addItem(UpdateCartItemRequest request);
    CartItemEntity updateItem(UpdateCartItemRequest request);
    void removeItem(String productItemId);
    CartEntity bulkRemoveItems(List<String> productItemIds);
    CartEntity getCurrentCart();
    void deleteByUserId(String userId);
}
