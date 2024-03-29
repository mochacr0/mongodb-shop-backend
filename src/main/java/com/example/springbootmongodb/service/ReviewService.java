package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.model.ReviewEntity;

public interface ReviewService {
    ReviewEntity save(ReviewEntity entity);
    ReviewEntity create(ReviewRequest request);
    ReviewEntity edit(String reviewId, ReviewRequest request);
    void disable(String reviewId);
    ReviewEntity findById(String reviewId);
    int countProductReviews(String productId);
    double calculateProductRatings(String productId);
    PageData<Review> findReviews(ReviewPageParameter pageParameter);
    ReviewEntity createResponse(String reviewId, ShopResponseRequest request);
    ReviewEntity editResponse(String reviewId, ShopResponseRequest request);
    ReviewEntity deleteResponse(String reviewId);
}
