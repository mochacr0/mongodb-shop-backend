package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Review;
import com.example.springbootmongodb.common.data.ReviewRequest;
import com.example.springbootmongodb.common.data.mapper.ReviewMapper;
import com.example.springbootmongodb.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @GetMapping(value = REVIEW_GET_REVIEW_BY_ID_ROUTE)
    @Operation(summary = "Truy xuất review theo id")
    Review findById(@PathVariable String reviewId) {
        return reviewMapper.fromEntity(reviewService.findById(reviewId));
    }

    @PostMapping(value = REVIEW_POST_REVIEW_ROUTE)
    @Operation(summary = "Đánh giá sản phẩm")
    Review post(@RequestBody ReviewRequest request) {
        return reviewMapper.fromEntity(reviewService.create(request));
    }

    @PutMapping(value = REVIEW_UPDATE_REVIEW_ROUTE)
    @Operation(summary = "Chỉnh sửa đánh giá")
    Review edit(@PathVariable String reviewId,
            @RequestBody ReviewRequest request) {
        return reviewMapper.fromEntity(reviewService.edit(reviewId, request));
    }
}
