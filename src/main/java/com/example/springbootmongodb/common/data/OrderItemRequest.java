package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemRequest {
    @Schema(description = "Id của item sản phẩm", example = "64b8dcd555960373f907f29e")
    private String productItemId;
    @Schema(description = "Số lượng muốn đặt", example = "1")
    private int quantity;
}
