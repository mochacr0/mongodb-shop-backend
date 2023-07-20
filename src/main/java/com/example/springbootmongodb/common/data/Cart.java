package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Cart {
    @Schema(description = "Id của user", example = "647d222a59a4582894a95c10")
    private String userId;
    @Schema(description = "Danh sách sản phẩm trong giỏ hàng")
    private List<CartItem> items = new ArrayList<>();
}
