package com.example.springbootmongodb.service;

import com.example.springbootmongodb.model.AbstractEntity;
import com.example.springbootmongodb.model.ToEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public abstract class DataBaseService<D extends ToEntity<E>, E extends AbstractEntity<D>> extends AbstractService {
    public abstract MongoRepository<E, String> getRepository();

//    public abstract Class<E> getEntityClass();

    D save(D data) {
        E entity = data.toEntity();
        if (entity.getCreatedAt() == null) {
            Optional<E> fetchedEntity = this.getRepository().findById(entity.getId());
            entity.setCreatedAt(fetchedEntity.map(AbstractEntity::getCreatedAt).orElse(null));
        }
        entity = this.getRepository().save(entity);
        return entity.toData();
    }

    D insert(D data) {
        E entity = data.toEntity();
        entity = this.getRepository().insert(entity);
        return entity.toData();
    }
}
