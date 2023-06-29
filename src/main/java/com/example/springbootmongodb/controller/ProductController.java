package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.PRODUCT_CREATE_PRODUCT_ROUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.PRODUCT_GET_PRODUCT_BY_ID_ROUTE;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductMapper mapper;
    @PostMapping(value = PRODUCT_CREATE_PRODUCT_ROUTE)
    Product create(@RequestBody ProductRequest request) {
        return mapper.fromEntity(productService.create(request));
    }

    @GetMapping(value = PRODUCT_GET_PRODUCT_BY_ID_ROUTE)
    Product getProductById (@PathVariable(name = "productId") String productId) {
        return mapper.fromEntity(productService.findById(productId));
    }
}
