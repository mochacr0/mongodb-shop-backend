package com.example.springbootmongodb.common.data.payment.momo;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MomoDeliveryInfo {
    private String deliveryAddress;
    private String deliveryFee;
    private String quantity;
}
