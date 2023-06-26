package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import com.example.springbootmongodb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final ProductRepository productRepository;
    private final ProductItemRepository itemRepository;

    @GetMapping(value = "/test")
    ProductEntity test() {
        ProductEntity product = ProductEntity.builder().name("product").build();
        product = productRepository.save(product);
        ProductItemEntity productItem = ProductItemEntity.builder().product(product).build();
        productItem = itemRepository.save(productItem);
        Optional<ProductItemEntity> productItemOpt = itemRepository.findById(productItem.getId());
        if (productItemOpt.isPresent()) {
            productItem = productItemOpt.get();
        }
        return productRepository.findById(product.getId()).get();
    }

    @GetMapping(value = "/test2")
    ProductItemEntity test2() {
        ProductEntity product = ProductEntity.builder().name("product").build();
        product = productRepository.save(product);
        ProductItemEntity productItem = ProductItemEntity.builder().product(product).build();
        productItem = itemRepository.save(productItem);
        return itemRepository.findById(productItem.getId()).get();
    }
}
