package com.example.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_VARIATION_COLLECTION_NAME;

@Document(value = PRODUCT_VARIATION_COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductVariationEntity extends AbstractEntity {
    private String name;
    private int index;
    @Field(name = "productId")
    @DocumentReference(lazy = true)
    private ProductEntity product;
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'variationId' : ?#{#self._id}}", lazy = true)
    List<VariationOptionEntity> options = new ArrayList<>();
}
