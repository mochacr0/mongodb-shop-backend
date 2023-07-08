package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.VariationOption;
import com.example.springbootmongodb.common.data.mapper.VariationOptionMapper;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.VariationOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@RestController()
@RequestMapping(value = "/test")
@RequiredArgsConstructor
public class TestController {
    private final ProductRepository productRepository;
    private final ProductItemRepository itemRepository;
    private final VariationOptionRepository optionRepository;
    private final MongoTemplate mongoTemplate;
    private final VariationOptionMapper optionMapper;
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

    @GetMapping(value = "/2")
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

    @GetMapping(value = "/3")
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

    @PostMapping(value = "/4")
    int test4(@RequestBody List<String> productIds) {
        return itemRepository.bulkDelete(productIds);
    }

    @GetMapping(value = "/5")
    VariationOption test5(@RequestParam String variationId) {
        return optionMapper.fromEntity(mongoTemplate.findOne(Query.query(where("variationId").in(Collections.singleton(variationId))), VariationOptionEntity.class));
    }
}
