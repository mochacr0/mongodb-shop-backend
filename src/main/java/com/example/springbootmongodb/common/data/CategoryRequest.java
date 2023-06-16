package com.example.springbootmongodb.common.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"isDefault", "default"}, allowGetters = true)
public class CategoryRequest {
    @Schema(title = "name", description = "Category name", example = "Shirt")
    private String name;
    @Schema(title = "parentCategoryId", description = "Parent category id", example = "64805c5bdb4a3449c81a9bed")
    private String parentCategoryId;
    @Schema(hidden = true)
    private boolean isDefault;

    public CategoryRequest(String name, String parentCategoryId) {
        this.name = name;
        this.parentCategoryId = parentCategoryId;
    }
}
