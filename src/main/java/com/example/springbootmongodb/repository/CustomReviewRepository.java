package com.example.springbootmongodb.repository;

public interface CustomReviewRepository {
    double calculateProductRatings(String productId);
    boolean isUserAllowedToPost(String userId, String productId);
}
