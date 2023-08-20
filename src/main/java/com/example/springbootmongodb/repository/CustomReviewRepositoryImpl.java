package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.ProductRatingsCalculation;
import com.example.springbootmongodb.model.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RequiredArgsConstructor
public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public double calculateProductRatings(String productId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("productId").is(productId)),
                group("productId").avg("rating").as("ratings"),
                project("ratings")
        );
        AggregationResults<ProductRatingsCalculation> results = mongoTemplate.aggregate(aggregation, ReviewEntity.class, ProductRatingsCalculation.class);
        ProductRatingsCalculation ratings = results.getMappedResults().get(0);
        return ratings.getRatings();
    }
}
