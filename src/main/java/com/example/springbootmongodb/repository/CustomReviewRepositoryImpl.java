package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.common.data.ProductRatingsCalculation;
import com.example.springbootmongodb.common.data.TotalOrderedItems;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.ReviewEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
@Slf4j
public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final MongoTemplate mongoTemplate;
    private static final int REVIEW_POST_WINDOW = 7;

    @Override
    public double calculateProductRatings(String productId) {
        Aggregation aggregation = newAggregation(
                match(where("productId").is(new ObjectId(productId))),
                group("productId").avg("rating").as("ratings"),
                project("ratings")
        );
        AggregationResults<ProductRatingsCalculation> results = mongoTemplate.aggregate(aggregation, ReviewEntity.class, ProductRatingsCalculation.class);
        ProductRatingsCalculation ratings = results.getMappedResults().get(0);
        return ratings.getRatings();
    }

    @Override
    public boolean isUserAllowedToPost(String userId, String productId) {
        long totalReviews = mongoTemplate.count(Query.query(where("user.id").is(userId).and("productId").is(productId)), ReviewEntity.class);
        Aggregation aggregation = newAggregation(
            match(where("userId").is(new ObjectId(userId))
                    //TODO: check order comment window
                    .and("currentStatus.state").is(OrderState.COMPLETED)
                    .and("currentStatus.createdAt").gte(LocalDateTime.now().minusDays(REVIEW_POST_WINDOW))
                    .and("orderItems.productId").is(productId)),
                unwind("orderItems"),
                count().as("totalOrdered")
        );
        AggregationResults<TotalOrderedItems> results = mongoTemplate.aggregate(aggregation, OrderEntity.class, TotalOrderedItems.class);
        if (CollectionUtils.isEmpty(results.getMappedResults())) {
            return false;
        }
        TotalOrderedItems totalOrderedItems = results.getMappedResults().get(0);
        log.info("Total reviews:" + totalReviews);
        log.info("Total ordered:" + totalOrderedItems.getTotalOrdered());
        return totalReviews < totalOrderedItems.getTotalOrdered();
    }
}
