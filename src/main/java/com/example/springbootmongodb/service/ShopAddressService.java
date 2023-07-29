package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ShopAddressRequest;
import com.example.springbootmongodb.model.ShopAddressEntity;

import java.util.List;

public interface ShopAddressService {
    ShopAddressEntity create(ShopAddressRequest request);
    ShopAddressEntity update(String id, ShopAddressRequest request);
    ShopAddressEntity findById(String id);
    ShopAddressEntity findDefaultAddress();
    List<ShopAddressEntity> findShopAddresses();
    void deleteById(String id);

}
