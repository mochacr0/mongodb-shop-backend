package com.example.springbootmongodb.service;

import com.example.springbootmongodb.model.AbstractEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public abstract class DataBaseService<E extends AbstractEntity> extends AbstractService {
    public abstract MongoRepository<E, String> getRepository();

    E save(E entity) {
        if (entity.getCreatedAt() == null) {
            Optional<E> fetchedEntity = this.getRepository().findById(entity.getId());
            entity.setCreatedAt(fetchedEntity.map(AbstractEntity::getCreatedAt).orElse(null));
        }
        entity = this.getRepository().save(entity);
        return entity;
    }

    E insert(E entity) {
        entity = this.getRepository().insert(entity);
        return entity;
    }
}
