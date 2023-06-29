package com.example.springbootmongodb.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_VARIATION_OPTION_COLLECTION_NAME;

@Document(value = PRODUCT_VARIATION_OPTION_COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class VariationOptionEntity extends AbstractEntity {
    private String name;
    private int index;
    @Field(name = "variationId")
    @DocumentReference(lazy = true)
    private ProductVariationEntity variation;
//    @Field(name = "itemIds")
//    @DocumentReference(lazy = true)
//    List<ProductItemEntity> items = new ArrayList<>();
}
