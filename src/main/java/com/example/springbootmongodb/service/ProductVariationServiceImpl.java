package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.data.mapper.ProductVariationMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.ProductVariationRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductVariationServiceImpl extends DataBaseService<ProductVariationEntity> implements ProductVariationService {
    private final ProductVariationRepository variationRepository;
    private final ProductVariationMapper variationMapper;
    private final ProductRepository productRepository;
    @Autowired
    @Lazy
    private VariationOptionService optionService;
    public static final String DUPLICATED_VARIANT_NAME_ERROR_MESSAGE = "Cannot create variations with same name";
    public static final String NON_EXISTENT_PRODUCT_ERROR_MESSAGE = "Cannot refer to a non-existent product";

    @Override
    public MongoRepository<ProductVariationEntity, String> getRepository() {
        return variationRepository;
    }

    @Override
    @Transactional
    public List<ProductVariationEntity> bulkCreate(List<ProductVariationRequest> requests, ProductEntity product) {
        log.info("Performing ProductVariationService bulkCreate");
        if (Collections.isEmpty(requests)) {
            throw new InvalidDataException("Product should have at least 1 variation");
        }
        if (containsDuplicates(requests, ProductVariationRequest::getName)) {
            throw new InvalidDataException(DUPLICATED_VARIANT_NAME_ERROR_MESSAGE);
        }
        if (!productRepository.existsById(product.getId())) {
            throw new UnprocessableContentException(NON_EXISTENT_PRODUCT_ERROR_MESSAGE);
        }
        List<ProductVariationEntity> newVariations = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ProductVariationEntity newVariation = variationMapper.toEntity(requests.get(i));
            newVariation.setProduct(product);
            newVariation.setIndex(i);
            newVariations.add(newVariation);
        }
        List<ProductVariationEntity> createdVariations = variationRepository.bulkCreate(newVariations);
        createdVariations.sort(new ProductVariationComparator());
        for (int i = 0; i < requests.size(); i++) {
            List<VariationOptionEntity> options = optionService.bulkCreate(requests.get(i).getOptions(), createdVariations.get(i));
            options.sort(new VariationOptionComparator());
            createdVariations.get(i).getOptions().addAll(options);
        }
        return createdVariations;
    }

    static class ProductVariationComparator implements Comparator<ProductVariationEntity> {
        @Override
        public int compare(ProductVariationEntity o1, ProductVariationEntity o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

    static class VariationOptionComparator implements Comparator<VariationOptionEntity> {
        @Override
        public int compare(VariationOptionEntity o1, VariationOptionEntity o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

}
