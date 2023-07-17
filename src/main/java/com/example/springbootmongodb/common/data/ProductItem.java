package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductItem extends AbstractData {
    @Schema(description = "Số lượng tồn kho", example = "100")
    private int quantity;
    @Schema(description = "Mô tả biến thể", example = "Kích cỡ:XXL, Màu:Trắng")
    private String variationDescription;
    @Schema(description = "URL ảnh", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String imageUrl;
    @Schema(description = "Đơn giá của item", example = "1000.0")
    private float price;
    private ProductSimplification product;
    private List<VariationOption> options;
}
