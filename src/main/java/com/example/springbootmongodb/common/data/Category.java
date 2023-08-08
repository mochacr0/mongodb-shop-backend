package com.example.springbootmongodb.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Category extends AbstractData {
    @Schema(description = "Tên danh mục", example = "Shirt")
    private String name;
    @Schema(description = "Id của danh mục chính", example = "64805c5bdb4a3449c81a9bed")
    private String parentCategoryId;
    private boolean isDefault;
    @Schema(hidden = true)
    @ReadOnlyProperty
    List<Category> subCategories;

    @Schema(name = "isDefault", description = "Có phải danh mục mặc định hay không", example = "false")
    @JsonProperty(value = "isDefault")
    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    @Schema(description = "Id danh muục", example = "647d222a59a4582894a95c10")
    public String getId() {
        return this.id;
    }

    @Override
    @Schema(description = "Thời điểm danh mục được tạo", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    @Schema(description = "Lần cập nhật danh mục gần nhất", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Category(String name, String parentCategoryId) {
        this.name = name;
        this.parentCategoryId = parentCategoryId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Category [id=");
        builder.append(this.id);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", parentCategoryId=");
        builder.append(this.parentCategoryId);
        builder.append(", isDefault=");
        builder.append(this.isDefault());
        builder.append("]");
        return builder.toString();
    }
}
