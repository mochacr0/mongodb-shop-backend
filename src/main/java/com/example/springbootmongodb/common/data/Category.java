package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.model.CategoryEntity;
import com.example.springbootmongodb.model.ToEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Category extends AbstractData implements ToEntity<CategoryEntity>  {
    @Schema(title = "name", description = "Category name", example = "Shirt")
    private String name;
    @Schema(title = "parentCategoryId", description = "Parent category id", example = "64805c5bdb4a3449c81a9bed")
    private String parentCategoryId;
    @Schema(title = "isDefault", description = "If this category is the default category, this boolean value will be true", example = "false")
    private boolean isDefault;
    @Schema(hidden = true)
    @ReadOnlyProperty
    List<Category> subCategories;

    @Override
    @Schema(title = "id", description = "Category ID", example = "647d222a59a4582894a95c10")
    public String getId() {
        return this.id;
    }

    @Override
    @Schema(title = "createdAt", description = "Timestamp of the category creation", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    @Schema(title = "updatedAt", description = "Timestamp of the category update", accessMode = Schema.AccessMode.READ_ONLY)
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
    @Override
    public CategoryEntity toEntity() {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(this.getName());
        entity.setParentCategoryId(this.getParentCategoryId());
        entity.setDefault(this.isDefault());
        return entity;
    }

    public static Category fromEntity(CategoryEntity entity) {
        return builder()
                .id(entity.getId())
                .name(entity.getName())
                .isDefault(entity.isDefault())
                .parentCategoryId(entity.getParentCategoryId())
                .subCategories(DaoUtils.toListData(entity.getSubCategories(), Category::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
