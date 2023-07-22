package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Product extends AbstractData {
    @Schema(description = "Tên sản phẩm", example = "Sản phẩm 1")
    private String name;
    @Schema(description = "ID của danh mục", example = "647d222a59a4582894a95c10")
    private String categoryId;
    @Schema(description = "Mô tả sản phẩm", example = "Mô tả 1")
    private String description;
    @Schema(description = "URL ảnh sản phẩm", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String imageUrl;
    @Schema(description = "Tổng số lượng đã bán", example = "100")
    private long totalSales;
    @Schema(description = "Đơn giá sản phẩm thấp nhất", example = "1000")
    private long minPrice;
    @Schema(description = "Đơn giá sản phẩm cao nhất", example = "9000")
    private long maxPrice;
    @Schema(description = "Đánh giá sản phẩm", example = "5")
    private float rating;
    @Schema(description = "Danh sách các item sản phẩm theo dạng map key-value, với key là mảng index biến thể")
    private Map<String, ProductItem> itemMap;
    @Schema(description = "Danh sách biến thể sản phẩm")
    private List<ProductVariation> variations;
}
