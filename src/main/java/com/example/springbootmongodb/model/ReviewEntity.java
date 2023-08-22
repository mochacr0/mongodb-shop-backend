package com.example.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.REVIEW_COLLECTION_NAME;

@Document(collection = REVIEW_COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ReviewEntity extends AbstractEntity {
    @DocumentReference
    @Field("userId")
    private UserEntity user;
    @DocumentReference(lazy = true)
    @Field(name = "productId")
    private ProductEntity product;
    private double rating;
    private String comment;
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
    private ReviewEntity shopResponse;
    private boolean isEdited;
    private boolean isDisabled;
}
