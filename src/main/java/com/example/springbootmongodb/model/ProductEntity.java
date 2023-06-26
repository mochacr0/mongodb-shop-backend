package com.example.springbootmongodb.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_COLLECTION_NAME;

@Document(collection = PRODUCT_COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductEntity extends AbstractEntity {
    private String name;
    private String description;
    private long totalSales;
    private float rating;
}
