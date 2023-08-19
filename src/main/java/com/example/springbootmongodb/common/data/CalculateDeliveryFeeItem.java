package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CalculateDeliveryFeeItem {
    @Schema(description = "Id của sản phẩm muốn đặt")
    private String productId;
    @Schema(description = "Số lượng sản phẩm muốn đặt")
    private int quantity;
}
