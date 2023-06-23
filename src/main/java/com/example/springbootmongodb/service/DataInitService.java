package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Category;
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
            Category category = Category
                    .builder()
                    .name(CATEGORY_DEFAULT_CATEGORY_NAME)
                    .isDefault(true)
                    .parentCategoryId(null)
                    .build();
            categoryService.create(category);
        }
    }
}
