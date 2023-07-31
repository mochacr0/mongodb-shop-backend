package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GHTKPickUpAddress {
    @JsonAlias(value = "pick_address_id")
    private String pickAddressId;
    private String address;
    @JsonAlias(value = "pick_tel")
    private String pickTel;
    @JsonAlias(value = "pick_name")
    private String pickName;
}
