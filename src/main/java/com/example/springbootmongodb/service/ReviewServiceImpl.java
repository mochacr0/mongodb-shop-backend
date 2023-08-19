package com.example.springbootmongodb.service;

import com.example.springbootmongodb.model.ReviewEntity;
import com.example.springbootmongodb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl extends DataBaseService<ReviewEntity> implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public MongoRepository<ReviewEntity, String> getRepository() {
        return reviewRepository;
    }
}
