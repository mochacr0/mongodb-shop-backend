package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;

public interface CategoryService {
    Category findById(String id);
    Category findByName(String name);
    PageData<Category> findCategories(PageParameter pageParameter);
    Category create(Category category);
    Category save(String id, Category category);
    boolean existsById(String id);
    void deleteById(String id);
    Category findDefaultCategory();
}
