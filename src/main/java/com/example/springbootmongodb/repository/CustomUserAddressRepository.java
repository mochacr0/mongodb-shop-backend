package com.example.springbootmongodb.repository;

import java.util.List;

public interface CustomUserAddressRepository {
    void deleteUserAddressesByUserId(String userId);
    long countByUserId(String userId);
}
