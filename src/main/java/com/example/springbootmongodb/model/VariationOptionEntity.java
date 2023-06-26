package com.example.springbootmongodb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_VARIATION_OPTION_COLLECTION_NAME;

@Document(value = PRODUCT_VARIATION_OPTION_COLLECTION_NAME)
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class VariationOptionEntity extends AbstractEntity {
    private String name;
    private int index;
    @Field(name = "variantId")
    @DocumentReference(lookup = "{'_id': ?#{#target}}")
    private ProductVariationEntity variation;
}
