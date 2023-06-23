package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.UserAddress;

import java.util.List;

public interface UserAddressService {
    UserAddress create(UserAddress address);
    UserAddress save(String addressId, UserAddress address);
    List<UserAddress> findUserAddressesByUserId(String userId);
    List<UserAddress> findCurrentUserAddresses();
    void deleteById(String addressId);
    UserAddress findById(String addressId);
    void deleteUserAddressesByUserId(String userId);
}
