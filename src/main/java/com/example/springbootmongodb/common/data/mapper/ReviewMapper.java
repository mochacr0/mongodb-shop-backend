package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.Review;
import com.example.springbootmongodb.common.data.ReviewRequest;
import com.example.springbootmongodb.model.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    private final UserMapper userMapper;

    public ReviewEntity toEntity(ReviewRequest request) {
        return ReviewEntity
                .builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .imageUrls(request.getImageUrls())
                .build();
    }

    public Review fromEntity(ReviewEntity entity) {
       return Review
               .builder()
               .id(entity.getId())
               .rating(entity.getRating())
               .comment(entity.getComment())
               .imageUrls(entity.getImageUrls())
               .isEdited(entity.isEdited())
               .shopResponse(fromEntityToShopResponse(entity.getShopResponse()))
               .createdAt(entity.getCreatedAt())
               .updatedAt(entity.getUpdatedAt())
               .user(userMapper.fromEntityToUserSimplification(entity.getUser()))
               .build();
    }

    public Review fromEntityToShopResponse(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        return Review
                .builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateFields(ReviewEntity entity, ReviewRequest request) {
        entity.setRating(request.getRating());
        entity.setComment(request.getComment());
        entity.setImageUrls(request.getImageUrls());
    }
}
