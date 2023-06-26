package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.CATEGORY_COLLECTION_NAME;

@Document(collection = CATEGORY_COLLECTION_NAME)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryEntity extends AbstractEntity {
    @Indexed(unique = true)
    private String name;
    @Field(targetType = FieldType.OBJECT_ID)
    private String parentCategoryId;
    private boolean isDefault;
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'parentCategoryId' : ?#{#self._id}}")
    List<CategoryEntity> subCategories;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CategoryEntity [id=");
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
