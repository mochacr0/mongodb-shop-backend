package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TemporaryImage {
    @Schema(description = "URL ảnh sản phẩm", example = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/fc909db6-eade-4980-b8af-d328786fd882.jpeg")
    private String url;
    @Schema(description = "Id của process. Mỗi khi tạo mới hoặc cập nhật sản phẩm mà có upload ảnh mới thì đính kèm field này vào payload")
    private String processId;
}
