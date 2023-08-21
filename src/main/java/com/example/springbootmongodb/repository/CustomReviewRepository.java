package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.Review;
import com.example.springbootmongodb.common.data.ReviewPageParameter;

public interface CustomReviewRepository {
    double calculateProductRatings(String productId);
    boolean isUserAllowedToPost(String userId, String productId);
    PageData<Review> findReviews(ReviewPageParameter pageParameter);

}
