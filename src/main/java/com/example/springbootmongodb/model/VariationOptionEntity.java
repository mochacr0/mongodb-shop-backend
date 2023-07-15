package com.example.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_VARIATION_OPTION_COLLECTION_NAME;

@Document(value = PRODUCT_VARIATION_OPTION_COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class VariationOptionEntity extends AbstractEntity {
    private String name;
    private String imageUrl;
    private int index;
    private boolean isDisabled;
    @Field(name = "variationId", targetType = FieldType.OBJECT_ID)
    @DocumentReference(lazy = true)
    @JsonBackReference
    private ProductVariationEntity variation;
}
