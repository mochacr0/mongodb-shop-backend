package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ReviewMapper;
import com.example.springbootmongodb.common.validator.CommonValidator;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ReviewEntity;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.springbootmongodb.common.validator.ConstraintValidator.validateFields;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl extends DataBaseService<ReviewEntity> implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MediaService mediaService;
    private final ReviewMapper reviewMapper;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final CommonValidator commonValidator;

    @Autowired
    @Lazy
    private ProductService productService;

    public static final String RATING_VIOLATION_ERROR_MESSAGE = "Product rating must be between 1 and 5";
    public static final String UNEDITABLE_AFTER_FIRST_EDIT_ERROR_MESSAGE = "You can only edit your review once";
    public static final String EXPIRED_EDIT_WINDOW_ERROR_MESSAGE = "Edit window ended after 7 days from posting";
    public static final int REVIEW_EDIT_WINDOW = 7;

    @Override
    public MongoRepository<ReviewEntity, String> getRepository() {
        return reviewRepository;
    }

    @Override
    public ReviewEntity save(ReviewEntity entity) {
        log.info("Performing ReviewService save");
        return super.save(entity);
    }

    @Override
    @Transactional
    public ReviewEntity create(ReviewRequest request) {
        log.info("Performing ReviewService create");
        validateFields(request);
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InvalidDataException(RATING_VIOLATION_ERROR_MESSAGE);
        }
        ProductEntity product;
        try {
            product = productService.findById(request.getProductId());
        } catch(ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }

        UserEntity user = userService.findCurrentUser();
        if (!reviewRepository.isUserAllowedToPost(user.getId(), product.getId())) {
            throw new InvalidDataException("You need to buy this product first");
        }
        ReviewEntity review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setProduct(product);
        review = super.insert(review);
        productService.updateRatings(review.getProduct().getId(), request.getRating());
        mediaService.persistCreatingReviewImages(request);
        return review;
    }

    @Override
    @Transactional
    public ReviewEntity edit(String reviewId, ReviewRequest request) {
        log.info("Performing ReviewService edit");
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InvalidDataException(RATING_VIOLATION_ERROR_MESSAGE);
        }
        ReviewEntity review = findById(reviewId);
        if (review.isEdited()) {
            throw new InvalidDataException(UNEDITABLE_AFTER_FIRST_EDIT_ERROR_MESSAGE);
        }
        if (LocalDateTime.now().isAfter(review.getCreatedAt().plusDays(REVIEW_EDIT_WINDOW))) {
            throw new InvalidDataException(EXPIRED_EDIT_WINDOW_ERROR_MESSAGE);
        }
        if (request.getRating() != review.getRating()) {
            productService.updateRatings(review.getProduct().getId(), request.getRating());
        }

        //review's images have not changed
        if (StringUtils.isEmpty(request.getProcessId())) {
            reviewMapper.updateFields(review, request);
            review.setEdited(true);
            return save(review);
        }

        //review's images have changed
        ReviewEntity odlReview;
        try {
            odlReview = objectMapper.readValue(objectMapper.writeValueAsString(review), ReviewEntity.class);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Encountered errors while copying object");
        }
        reviewMapper.updateFields(review, request);
        review.setEdited(true);
        save(review);
        mediaService.persistUpdatingReviewImages(request, odlReview);
        return review;
    }

    @Override
    public void disable(String reviewId) {
        log.info("Performing ReviewService delete");
        ReviewEntity review = findById(reviewId);
        review.setDisabled(true);
        save(review);
    }

    @Override
    public ReviewEntity findById(String reviewId) {
        log.info("Performing ReviewService findById");
        if (StringUtils.isEmpty(reviewId)) {
            throw new InvalidDataException("Review Id should be specified");
        }
        ReviewEntity review = reviewRepository.findById(reviewId).get();
        if (review == null || review.isDisabled()) {
            throw new ItemNotFoundException(String.format("Review with id [%s] is not found", reviewId));
        }
        return review;
    }

    @Override
    public int countProductReviews(String productId) {
        log.info("Performing ReviewService countProductReviews");
        return reviewRepository.countByProductId(productId);
    }

    @Override
    public double calculateProductRatings(String productId) {
        log.info("Performing ReviewService calculateProductRatings");
        return reviewRepository.calculateProductRatings(productId);
    }

    @Override
    public PageData<Review> findReviews(ReviewPageParameter pageParameter) {
        log.info("Performing ReviewService findReviews");
        commonValidator.validatePageParameter(pageParameter);
        return reviewRepository.findReviews(pageParameter);
    }

    @Override
    public ReviewEntity createResponse(String reviewId, ShopResponseRequest request) {
        log.info("Performing ReviewService createResponse");
        validateFields(request);
        ReviewEntity review = findById(reviewId);
        ReviewEntity shopResponse = null;
        if (review.getShopResponse() != null) {
            shopResponse = review.getShopResponse();
            shopResponse.setComment(request.getComment());
        }
        else {
            shopResponse = reviewMapper.fromRequestToShopResponse(request);
        }
        review.setShopResponse(shopResponse);
        return save(review);
    }

    @Override
    public ReviewEntity editResponse(String reviewId, ShopResponseRequest request) {
        log.info("Performing ReviewService createResponse");
        validateFields(request);
        ReviewEntity review = findById(reviewId);
        ReviewEntity shopResponse = review.getShopResponse();
        if (shopResponse == null) {
            throw new InvalidDataException(String.format("Response of review with id [%s] is not found", reviewId));
        }
        shopResponse.setComment(request.getComment());
        review.setShopResponse(shopResponse);
        return save(review);
    }

    @Override
    public ReviewEntity deleteResponse(String reviewId) {
        log.info("Performing ReviewService deleteResponse");
        ReviewEntity review = findById(reviewId);
        review.setShopResponse(null);
        return save(review);
    }
}
