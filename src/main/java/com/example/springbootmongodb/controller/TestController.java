package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.VariationOptionRepository;
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
    private final VariationOptionRepository optionRepository;
//    @GetMapping(value = "/test")
//    ProductEntity test() {
//        ProductEntity product = ProductEntity.builder().name("product").build();
//        product = productRepository.save(product);
//        ProductItemEntity productItem = ProductItemEntity.builder().product(product).build();
//        productItem = itemRepository.save(productItem);
//        Optional<ProductItemEntity> productItemOpt = itemRepository.findById(productItem.getId());
//        if (productItemOpt.isPresent()) {
//            productItem = productItemOpt.get();
//        }
//        return productRepository.findById(product.getId()).get();
//    }

    @GetMapping(value = "/test2")
    String test2() {
        ProductEntity product = new ProductEntity();
        product.setName("product");
        product = productRepository.save(product);
        ProductItemEntity item = new ProductItemEntity();
        item.setProduct(product);
        itemRepository.save(item);
        item = itemRepository.findById(item.getId()).get();
        product.getItems().add(item);
        ProductEntity savedProduct = productRepository.save(product);
//        product = productRepository.findById(product.getId()).get();
        return null;
    }

    @GetMapping(value = "/test3")
    ProductItemEntity test3() {
//        ProductItemEntity item = itemRepository.save(new ProductItemEntity());
//        VariationOptionEntity option = optionRepository.save(new VariationOptionEntity());
//        item.getOptions().add(option);
//        ProductItemEntity savedItem = itemRepository.save(item);
//        option.getItems().add(item);
//        VariationOptionEntity savedOption = optionRepository.save(option);
//        ProductItemEntity retrievedItem = itemRepository.findById(savedItem.getId()).get();
//        VariationOptionEntity retrievedOption = optionRepository.findById(savedOption.getId()).get();
//        itemRepository.delete(retrievedItem);
//        retrievedOption = optionRepository.findById(savedOption.getId()).get();
        return null;
    }
}
