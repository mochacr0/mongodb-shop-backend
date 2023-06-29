package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductItem;
import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.common.data.mapper.ProductItemMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductItemServiceImpl implements ProductItemService {
    private final ProductItemRepository itemRepository;
    private final ProductItemMapper mapper;
    @Override
    @Transactional
    public List<ProductItemEntity> bulkCreate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations) {
        log.info("Performing ProductItemService bulkCreate");
        if (Collections.isEmpty(requests)) {
            throw new InvalidDataException("Product items should not be empty");
        }
        if (Collections.isEmpty(variations)) {
            throw new InvalidDataException("Cannot create items with no variations");
        }
        ProductEntity product = variations.get(0).getProduct();
        List<ProductItemEntity> newItems = new ArrayList<>();
        for (ProductItemRequest request : requests) {
            ProductItemEntity newItem = mapper.toEntity(request);
            List<VariationOptionEntity> newItemOptions = new ArrayList<>();
            for (int i = 0; i < request.getVariationIndex().size(); i++) {
                newItemOptions.add(variations.get(i).getOptions().get(request.getVariationIndex().get(i)));
            }
            newItem.getOptions().addAll(newItemOptions);
            newItem.setProduct(product);
            newItems.add(newItem);
        }
        return itemRepository.bulkCreate(newItems);
    }
}
