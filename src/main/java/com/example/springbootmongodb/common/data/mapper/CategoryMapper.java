package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.model.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public void updateFields(CategoryEntity entity, Category category) {
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setDefault(category.isDefault());
        entity.setParentCategoryId(category.getParentCategoryId());
    }
    public CategoryEntity toEntity(Category category) {
        return CategoryEntity
                .builder()
                .name(category.getName())
                .parentCategoryId(category.getParentCategoryId())
                .isDefault(category.isDefault())
                .build();
    }

    public Category fromEntity(CategoryEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isDefault(entity.isDefault())
                .parentCategoryId(entity.getParentCategoryId())
                .subCategories(DaoUtils.toListData(entity.getSubCategories(), this::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
