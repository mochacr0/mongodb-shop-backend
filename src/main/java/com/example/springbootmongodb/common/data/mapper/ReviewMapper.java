package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.Review;
import com.example.springbootmongodb.model.ReviewEntity;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    Review fromEntity(ReviewEntity entity) {
       return Review
               .builder()
               .id(entity.getId())
               .rating(entity.getRating())
               .comment(entity.getComment())
               .shopResponse(fromEntityToShopResponse(entity.getShopResponse()))
               .createdAt(entity.getCreatedAt())
               .updatedAt(entity.getUpdatedAt())
               .build();
    }

    Review fromEntityToShopResponse(ReviewEntity entity) {
        return Review
                .builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
