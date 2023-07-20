package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateCartItemRequest {
    @Schema(description = "Id của mặt hàng sản phẩm", example = "647d222a59a4582894a95c10")
    private String productItemId;
    @Positive
    @Schema(description = "Số lượng mặt hàng muốn thêm/cập nhật", example = "10")
    private int quantity;
}
