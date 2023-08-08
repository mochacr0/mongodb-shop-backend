package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKProductRequest {
    private String name;
    private int price;
    private double weight;
    private int quantity;
    @JsonProperty("product_code")
    private int productCode;
}
