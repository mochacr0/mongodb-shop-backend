package com.example.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_ITEM_COLLECTION_NAME;

@Document(value = PRODUCT_ITEM_COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductItemEntity extends AbstractEntity {
    private int sku;
    private float price;
    @Field(name = "productId")
    @DocumentReference(lazy = true)
    private ProductEntity product;
    @Field(name = "optionIds")
    @DocumentReference(lazy = true)
    private List<VariationOptionEntity> options = new ArrayList<>();

}
