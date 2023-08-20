package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ReviewRequest;
import com.example.springbootmongodb.model.ReviewEntity;

public interface ReviewService {
    ReviewEntity save(ReviewEntity entity);
    ReviewEntity create(ReviewRequest request);
    ReviewEntity edit(String reviewId, ReviewRequest request);
    ReviewEntity findById(String reviewId);
    int countProductReviews(String productId);
    double calculateProductRatings(String productId);
}
