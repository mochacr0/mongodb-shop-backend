package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ProductPageParameter extends PageParameter {
    @Schema(description = "Đơn giá sản phẩm thấp nhất", example = "1000")
    private Float minPrice;
    @Schema(description = "Đơn giá sản phẩm cao nhất", example = "9000")
    private Float maxPrice;
    @Schema(description = "Đánh giá sản phẩm", example = "5")
    private Float rating;
    @Schema(description = "Id của danh mục sản phẩm", example = "5")
    private String categoryId;
}
