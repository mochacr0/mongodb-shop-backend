package com.example.springbootmongodb.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_COLLECTION_NAME;

@Document(collection = PRODUCT_COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductEntity extends AbstractEntity {
    private String name;
    private String description;
    private long totalSales;
    private float rating;
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'productId' : ?#{#self._id}}", lazy = true)
    List<ProductItemEntity> items = new ArrayList<>();
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'productId' : ?#{#self._id}}", lazy = true)
    List<ProductVariationEntity> variations = new ArrayList<>();
}
