package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.CategoryEntity;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCategoryRepository {
    CategoryEntity findDefaultCategory();
    UpdateResult detachChildCategoriesFromParent(String parentCategoryId);
}
