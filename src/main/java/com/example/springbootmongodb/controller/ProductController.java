package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductMapper mapper;
    @PostMapping(value = PRODUCT_CREATE_PRODUCT_ROUTE)
    Product createAsync(@RequestBody ProductRequest request) {
        return mapper.fromEntity(productService.createAsync(request));
    }

//    @PostMapping(value = PRODUCT_CREATE_PRODUCT_ROUTE)
//    Product create(@RequestBody ProductRequest request) {
//        return mapper.fromEntity(productService.create(request));
//    }

    @GetMapping(value = PRODUCT_GET_PRODUCT_BY_ID_ROUTE)
    Product getProductById (@PathVariable(name = "productId") String productId) {
        return mapper.fromEntity(productService.findById(productId));
    }

    @PutMapping(value = PRODUCT_UPDATE_PRODUCT_ROUTE)
    Product update (@PathVariable(name = "productId") String productId,
                    @RequestBody ProductRequest request) {
        return mapper.fromEntity(productService.updateAsync(productId, request));
    }

    @Operation(summary = "Return a page of available products")
    @GetMapping(value = PRODUCT_GET_PRODUCTS_ROUTE)
    PageData<ProductPaginationResult> getProducts(@Parameter(description = PAGE_NUMBER_DESCRIPTION)
                                                  @RequestParam(defaultValue = PAGE_NUMBER_DEFAULT_STRING_VALUE) int page,
                                                  @Parameter(description = PAGE_SIZE_DESCRIPTION)
                                                  @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_STRING_VALUE) int pageSize,
                                                  @Parameter(description = SORT_ORDER_DESCRIPTION,
                                                          examples = {@ExampleObject(name = "asc (Ascending)", value = "asc"),
                                                                  @ExampleObject(name = "desc (Descending)", value = "desc")})
                                                  @RequestParam(required = false) String sortDirection,
                                                  @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                                                  @RequestParam(required = false) String sortProperty,
////                                                  @Parameter(description = MIN_PRICE_FILTER_DESCRIPTION)
                                                  @RequestParam(required = false) Float minPrice,
////                                                  @Parameter(description = MAX_PRICE_FILTER_DESCRIPTION)
                                                  @RequestParam(required = false) Float maxPrice,
//                                                  @Parameter(description = RATING_FILTER_DESCRIPTION)
                                                  @RequestParam(required = false) Float rating,
                                                  @RequestParam(required = false) String categoryId) {
        ProductPageParameter pageParameter = ProductPageParameter
                .builder()
                .page(page)
                .pageSize(pageSize)
                .sortDirection(sortDirection)
                .sortProperty(sortProperty)
                .rating(rating)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoryId(categoryId)
                .build();
        return productService.findProducts(pageParameter);
    }

    @Operation(summary = "Search product with given text search string and search limit")
    @GetMapping(value = PRODUCT_SEARCH_PRODUCTS_ROUTE)
    List<ProductSearchResult> searchProducts(@RequestParam(required = false) String textSearch,
                                 @RequestParam(required = false) Integer limit) {
        return DaoUtils.toListData(productService.searchProducts(textSearch, limit), mapper::fromEntityToSearchResult);
    }

    @DeleteMapping(value = PRODUCT_DELETE_PRODUCT_BY_ID_ROUTE)
    void deleteProductById(@PathVariable(name = "productId") String productId) {
        productService.deleteById(productId);
    }
}
