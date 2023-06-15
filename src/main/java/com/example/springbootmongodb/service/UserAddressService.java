package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.UserAddressRequest;

import java.util.List;

public interface UserAddressService {
    UserAddress create(UserAddressRequest userAddressRequest);
    UserAddress save(String addressId, UserAddressRequest userAddressRequest);
    List<UserAddress> findUserAddressesByUserId(String userId);
    List<UserAddress> findCurrentUserAddresses();
    void deleteById(String addressId);
    UserAddress findById(String addressId);
    void deleteUserAddressesByUserId(String userId);
}
