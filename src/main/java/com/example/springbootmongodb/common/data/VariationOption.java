package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.data.mapper.ProductVariationSimplification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class VariationOption extends AbstractData {
    @Schema(description = "Tên phân loại", example = "XXL")
    private String name;
    @Schema(description = "URL ảnh", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String imageUrl;
    @Schema(description = "Index của phân loại này trong mảng phân loại của biến thể", example = "0")
    private int index;
    private ProductVariationSimplification variation;
}
