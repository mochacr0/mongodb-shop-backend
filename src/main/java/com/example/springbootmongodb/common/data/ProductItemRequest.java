package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemRequest {
    @Schema(description = "Id của item sản phẩm. Để trống khi tạo item mới")
    private String id;
    @Schema(description = "Số lượng item", example = "100")
    @Positive
    private int quantity;
    @Schema(description = "Giá của item", example = "1000.0")
    @Positive
    private long price;
    @Schema(description = "Tổ hợp index của biến thể sản phẩm", example = "[0,1]")
    private List<Integer> variationIndex = new ArrayList<>();
}
