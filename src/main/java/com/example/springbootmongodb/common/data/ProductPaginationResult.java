package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ProductPaginationResult extends AbstractData {
    @Schema(name = "name", description = "Tên sản phẩm", example = "Sản phẩm 1")
    private String name;
    @Schema(name = "imageUrl", description = "Url ảnh sản phẩm", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String imageUrl;
    @Schema(name = "totalSales", description = "Tổng số lượng đã bán", example = "100")
    private long totalSales;
    @Schema(name = "rating", description = "Đánh giá sản phẩm", example = "5")
    private double rating;
    @Schema(name = "minPrice", description = "Đơn giá sản phẩm thấp nhất", example = "1000")
    private float minPrice;
    @Schema(name = "maxPrice", description = "Đơn giá sản phẩm cao nhất", example = "9000")
    private float maxPrice;
    @Schema(name = "categoryId", description = "ID của danh mục")
    private String categoryId;

}
