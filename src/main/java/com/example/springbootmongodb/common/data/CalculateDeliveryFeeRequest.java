package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CalculateDeliveryFeeRequest {
    @Schema(description = "Id địa chỉ của người dùng cần tính phí")
    private String userAddressId;
    private List<CalculateDeliveryFeeItem> items;
}
