package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductVariation;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.ProductVariationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductVariationServiceImpl extends DataBaseService<ProductVariationEntity> implements ProductVariationService {
    private final ProductVariationRepository variationRepository;
    private final ProductRepository productRepository;
    public static final String DUPLICATED_VARIANT_NAME_ERROR_MESSAGE = "Cannot create variations with same name";
    public static final String NON_EXISTENT_PRODUCT_ERROR_MESSAGE = "Cannot refer to a non-existent product";

    @Override
    public MongoRepository<ProductVariationEntity, String> getRepository() {
        return variationRepository;
    }

    @Override
    public List<ProductVariation> bulkCreate(List<ProductVariationRequest> requests, String productId) {
        log.info("Performing ProductVariationService bulkCreate");
        if (containsDuplicates(requests, ProductVariationRequest::getName)) {
            throw new InvalidDataException(DUPLICATED_VARIANT_NAME_ERROR_MESSAGE);
        }
        Optional<ProductEntity> existingProductEntityOpt = productRepository.findById(productId);
        if (existingProductEntityOpt.isEmpty()) {
            throw new UnprocessableContentException(NON_EXISTENT_PRODUCT_ERROR_MESSAGE);
        }
        List<ProductVariationEntity> entities = requests.stream().map(request -> {
            request.setProductId(productId);
            return request.toEntity();
        }).collect(Collectors.toList());
        return DaoUtils.toListData(variationRepository.bulkCreate(entities), ProductVariation::fromEntity);
    }
}
