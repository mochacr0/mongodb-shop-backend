package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.CategoryEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<CategoryEntity,String>, CustomCategoryRepository {
    @NonNull
    @Query("{'parentCategoryId' : null}")
    Page<CategoryEntity> findParentCategories(@NonNull Pageable pageable);
    Optional<CategoryEntity> findByName(String name);
    boolean existsByName(String name);
}
