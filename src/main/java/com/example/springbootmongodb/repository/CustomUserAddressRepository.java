package com.example.springbootmongodb.repository;

public interface CustomUserAddressRepository {
    void deleteUserAddressesByUserId(String userId);
    long countByUserId(String userId);
}
