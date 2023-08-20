package com.example.springbootmongodb.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
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
    @DocumentReference(lazy = true)
    @Field(name = "productId")
    private ProductEntity product;
    private double rating;
    private String comment;
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
    @DocumentReference
    private ReviewEntity shopResponse;
    private boolean isEdited;
}