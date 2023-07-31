package com.example.springbootmongodb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.example.springbootmongodb.model.ModelConstants.SHOP_ADDRESS_COLLECTION_NAME;

@Document(collection = SHOP_ADDRESS_COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ShopAddressEntity extends AbstractEntity {
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String streetAddress;
    private boolean isDefault;
}
