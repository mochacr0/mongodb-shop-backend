package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductRequest {
    @Schema(description = "Id của sản phẩm. Để trống khi tạo sản phẩm mới")
    private String id;
    @Schema(description = "Id của process, được trả về sau mỗi lần gọi API upload ảnh")
    private String processId;
    @Schema(description = "Id của danh mục sản phẩm")
    private String categoryId;
    @Schema(description = "Tên sản phẩm", example = "Sản phẩm 1")
    @Required(fieldName = "Product name")
    private String name;
    @Schema(description = "Mô tả sản phẩm", example = "Mô tả 1")
    private String description;
    @Schema(description = "Url ảnh sản phẩm", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String imageUrl;
    @Schema(description = "Cân nặng sau khi đóng gói", example = "0.1")
    @Required(fieldName = "Product weight")
    private Double weight;
    @Schema(description = "Danh sách biến thể")
    private List<ProductVariationRequest> variations = new ArrayList<>();
    @Schema(description = "Danh sách item")
    private List<ProductItemRequest> items = new ArrayList<>();
}
