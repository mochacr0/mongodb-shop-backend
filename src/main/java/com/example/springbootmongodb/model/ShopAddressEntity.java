package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.HasAddress;
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
public class ShopAddressEntity extends AbstractEntity implements HasAddress {
    private String name;
    private String phoneNumber;
    private String addressDetails;
    private String province;
    private String district;
    private String ward;
    private String hamlet;
    private String street;
    private boolean isDefault;
}
