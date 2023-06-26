package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.model.CategoryEntity;

public interface CategoryService {
    CategoryEntity findById(String id);
    CategoryEntity findByName(String name);
    PageData<Category> findCategories(PageParameter pageParameter);
    CategoryEntity create(Category category);
    CategoryEntity save(String id, Category category);
    boolean existsById(String id);
    void deleteById(String id);
    CategoryEntity findDefaultCategory();
    boolean existsByName(String name);
}
