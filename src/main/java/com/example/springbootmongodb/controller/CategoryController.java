package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.CategoryRequest;
import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.controller.ControllerConstants.SORT_PROPERTY_DEFAULT_VALUE;

@RestController
@Tag(name = "Category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @Operation(summary = "Return a page of available users")
    @GetMapping(value = CATEGORY_GET_CATEGORIES_ROUTE)
    PageData<Category> getCategories(@Parameter(description = PAGE_NUMBER_DESCRIPTION)
                                     @RequestParam(defaultValue = PAGE_NUMBER_DEFAULT_STRING_VALUE) int page,
                                     @Parameter(description = PAGE_SIZE_DESCRIPTION)
                                     @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_STRING_VALUE) int pageSize,
                                     @Parameter(description = SORT_ORDER_DESCRIPTION,
                                             examples = {@ExampleObject(name = "asc (Ascending)", value = "asc"),
                                                     @ExampleObject(name = "desc (Descending)", value = "desc")})
                                     @RequestParam(defaultValue = SORT_DIRECTION_DEFAULT_VALUE) String sortDirection,
                                     @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                                     @RequestParam(defaultValue = SORT_PROPERTY_DEFAULT_VALUE) String sortProperty) {
        return categoryService.findCategories(new PageParameter(page, pageSize, sortDirection, sortProperty, ""));
    }

    @Operation(summary = "Retrieve a specific category by the provided categoryId")
    @GetMapping(value = CATEGORY_GET_CATEGORY_BY_ID_ROUTE)
    Category getCategoryById(@Parameter(description = "ID of the category to retrieve", required = true)
                             @PathVariable(name = "categoryId") String categoryId) {
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new InvalidDataException(String.format("Category with id [%s] is not found",categoryId));
        }
        return category;
    }

    @Operation(summary = "Retrieve the default category")
    @GetMapping(value = CATEGORY_GET_DEFAULT_CATEGORY_ROUTE)
    Category getDefaultCategory() {
        return categoryService.findDefaultCategory();
    }

    @Operation(summary = "Create a new category")
    @PostMapping(value = CATEGORY_CREATE_CATEGORY_ROUTE)
    Category create(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                          description = "Category object containing the category details")
                    @RequestBody CategoryRequest categoryRequest) {
        return categoryService.create(categoryRequest);
    }
    @Operation(summary = "Update an existing category by the provided categoryId")
    @PutMapping(value = CATEGORY_UPDATE_CATEGORY_ROUTE)
    Category update(@Parameter(description = "ID of the category to update", required = true)
                    @PathVariable(name = "categoryId") String categoryId,
                    @RequestBody CategoryRequest categoryRequest) {
        return categoryService.save(categoryId, categoryRequest);
    }
    @Operation(summary = "Delete an existing category by the provided categoryId")
    @DeleteMapping(value = CATEGORY_DELETE_CATEGORY_BY_ID_ROUTE)
    void deleteCategoryById(@Parameter(description = "ID of the category to delete", required = true)
                            @PathVariable(name = "categoryId") String categoryId) {
        categoryService.deleteById(categoryId);
    }
}
