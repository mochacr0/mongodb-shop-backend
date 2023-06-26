package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.PRODUCT_ITEM_COLLECTION_NAME;

@Document(value = PRODUCT_ITEM_COLLECTION_NAME)
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductItemEntity extends AbstractEntity{
    private int sku;
    private float price;
    private String index;
    @Field(name = "productId")
    @DocumentReference(lookup = "{'_id': ?#{#target}}")
    private ProductEntity product;
    @Field(name = "optionIds")
    @DocumentReference(lookup = "{'_id': ?#{#target}}")
    private List<VariationOptionEntity> options;

}
