package com.example.springbootmongodb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_VARIATION_COLLECTION_NAME;

@Document(value = PRODUCT_VARIATION_COLLECTION_NAME)
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductVariationEntity extends AbstractEntity {
    private String productId;
    private String name;
    private int index;
}
