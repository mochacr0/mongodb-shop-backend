package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.common.data.TemporaryImage;
import com.example.springbootmongodb.model.ProductEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    TemporaryImage uploadImage(String processId, MultipartFile image);
    void removeTemporaryTag(String imageName);
    void persistCreatingProductImagesAsync(ProductRequest productRequest);
    void persistUpdatingProductImagesAsync(ProductRequest productRequest, ProductEntity oldProduct);
}
