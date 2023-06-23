package com.example.springbootmongodb.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

import static com.example.springbootmongodb.model.ModelConstants.CREATED_AT_FIELD;
import static com.example.springbootmongodb.model.ModelConstants.UPDATED_AT_FIELD;

@Data
public class AbstractEntity {
    @Id
    protected String id;
    @CreatedDate
    @Field(name = CREATED_AT_FIELD)
    protected LocalDateTime createdAt;
    @LastModifiedDate
    @Field(name = UPDATED_AT_FIELD)
    protected LocalDateTime updatedAt;
}
