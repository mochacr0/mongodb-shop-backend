package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.model.UserAddressEntity;

import java.util.List;

public interface UserAddressService {
    UserAddressEntity create(UserAddress address);
    UserAddressEntity save(String addressId, UserAddress address);
    List<UserAddressEntity> findCurrentUserAddresses();
    void deleteById(String addressId);
    UserAddressEntity findById(String addressId);
    void deleteUserAddressesByUserId(String userId);
}
