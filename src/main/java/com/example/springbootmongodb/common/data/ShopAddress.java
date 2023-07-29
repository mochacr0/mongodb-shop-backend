package com.example.springbootmongodb.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ShopAddress extends AbstractData {
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String streetAddress;
    private boolean isDefault;
}
