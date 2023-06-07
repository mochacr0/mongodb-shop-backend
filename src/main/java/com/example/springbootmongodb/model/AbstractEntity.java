package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.ToData;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

import static com.example.springbootmongodb.model.ModelConstants.*;

@Data
public abstract class AbstractEntity<T> implements ToData<T> {
    @Id
    protected String id;
    @CreatedDate
    @Field(name = CREATED_AT_FIELD)
    protected LocalDateTime createdAt;
    @LastModifiedDate
    @Field(name = UPDATED_AT_FIELD)
    protected LocalDateTime updatedAt;
}
