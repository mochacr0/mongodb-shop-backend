package com.example.springbootmongodb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

import static com.example.springbootmongodb.model.ModelConstants.CART_COLLECTION_NAME;

@Document(collection = CART_COLLECTION_NAME)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class CartEntity extends AbstractEntity {
    private String userId;
    private Map<String, CartItemEntity> itemMap = new HashMap<>();
}
