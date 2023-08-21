package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.common.data.ReviewRequest;
import com.example.springbootmongodb.common.data.TemporaryImage;
import com.example.springbootmongodb.common.data.TemporaryImages;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductSavingProcessEntity;
import com.example.springbootmongodb.model.ReviewEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    TemporaryImage uploadImage(String processId, MultipartFile image);
    TemporaryImages uploadImages(String processId, List<MultipartFile> images);
    void removeTemporaryTag(String imageName);
    void persistCreatingProductImages(ProductRequest request);
    void persistUpdatingProductImages(ProductRequest request, ProductEntity oldProduct);
    void persistCreatingReviewImages(ReviewRequest request);
    void persistUpdatingReviewImages(ReviewRequest request, ReviewEntity oldReview);
    ProductSavingProcessEntity findProcessById(String processId);
}
