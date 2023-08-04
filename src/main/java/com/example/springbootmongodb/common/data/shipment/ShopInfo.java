package com.example.springbootmongodb.common.data.shipment;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShopInfo {
    private String name;
    private String email;
    private String address;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String hamlet;
    private String phoneNumber;
}
