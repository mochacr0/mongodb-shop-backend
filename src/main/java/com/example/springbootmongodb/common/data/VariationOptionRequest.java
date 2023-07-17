package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariationOptionRequest {
    @Schema(description = "Id của phân loại. Để trống khi tạo phân loại mới")
    private String id;
    @Schema(description = "Tên phân loại", example = "XXL")
    private String name;
    @Schema(description = "URL ảnh", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String imageUrl;
}
