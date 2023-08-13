package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
    @Schema(description = "Phương thức thanh toán. (momo hoặc cash)", example = "cash")
    private String paymentMethod;
    private List<OrderItemRequest> orderItems = new ArrayList<>();
    @Schema(description = "Ghi chú cho đơn hàng", example = "Gửi mail nếu không gọi được")
    private String note;
    private ShopAddress shopAddress;
    private UserAddress userAddress;
}
