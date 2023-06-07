package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.UserEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String>, CustomUserRepository {
    @NonNull
    Page<UserEntity> findAll(@NonNull Pageable pageable);
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByEmail(String email);

}
