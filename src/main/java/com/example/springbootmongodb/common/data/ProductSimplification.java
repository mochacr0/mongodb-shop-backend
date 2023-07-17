package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductSimplification {
    @Schema(description = "Id sản phẩm")
    private String id;
    @Schema(description = "Tên sản phẩm", example = "Sản phẩm 1")
    private String name;
}
