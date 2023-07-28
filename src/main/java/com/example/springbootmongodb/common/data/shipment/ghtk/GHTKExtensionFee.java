package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKExtensionFee {
    private String display;
    private String title;
    private int amount;
    private String type;
}
