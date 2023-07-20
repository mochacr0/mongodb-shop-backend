package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductVariationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductVariation extends AbstractData {
    @Schema(description = "Id sản phẩm", example = "647d222a59a4582894a95c10")
    private String productId;
    @Schema(description = "Tên biến thể", example = "Kích cỡ")
    private String name;
    @Schema(description = "Index của biến thể này trong mảng biến thể của sản phẩm", example = "0")
    private int index;
    @Schema(description = "Danh sách phân loại biến thể")
    private List<VariationOption> options;

    public static ProductVariation fromEntity(ProductVariationEntity entity) {
        return ProductVariation
                .builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .name(entity.getName())
                .index(entity.getIndex())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
