package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.CategoryEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<CategoryEntity,String>, CustomCategoryRepository {
    @NonNull
    Page<CategoryEntity> findAll(@NonNull Pageable pageable);
    Optional<CategoryEntity> findByName(String name);
}
