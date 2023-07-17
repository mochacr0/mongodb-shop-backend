package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class ProductVariationRequest {
    @Schema(description = "Id sản phẩm, để trống khi tạo biến thể mới")
    private String id;
    @Schema(description = "Id của sản phẩm")
    private String productId;
    @Schema(description = "Tên biến thể", example = "Color")
    private String name;
    @Schema(description = "Danh sách phân loại biến thể")
    List<VariationOptionRequest> options = new ArrayList<>();
}
