package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.springbootmongodb.model.ModelConstants.CATEGORY_DEFAULT_CATEGORY_NAME;

@Service
@RequiredArgsConstructor
public class DataInitService {
    private final CategoryService categoryService;
    public void init() {
        createDefaultCategory();
    }

    private void createDefaultCategory() {
        Category defaultCategory = categoryService.findDefaultCategory();
        if (defaultCategory == null) {
            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setName(CATEGORY_DEFAULT_CATEGORY_NAME);
            categoryRequest.setDefault(true);
            categoryRequest.setParentCategoryId(null);
            categoryService.create(categoryRequest);
        }
    }
}
