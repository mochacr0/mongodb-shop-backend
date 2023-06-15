package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.CategoryEntity;
import com.mongodb.client.result.UpdateResult;

public interface CustomCategoryRepository {
    CategoryEntity findDefaultCategory();
    UpdateResult detachChildCategoriesFromParent(String parentCategoryId);
}
