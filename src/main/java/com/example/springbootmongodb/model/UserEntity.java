package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.security.Authority;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.example.springbootmongodb.model.ModelConstants.EMAIL_FIELD;
import static com.example.springbootmongodb.model.ModelConstants.NAME_FIELD;

@Document(collection = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends AbstractEntity<User> {
    @Field(name = NAME_FIELD)
    @Indexed(unique = true)
    private String name;
    @Field(name = EMAIL_FIELD)
    @Indexed(unique = true)
    private String email;
    @Field(name = ModelConstants.AUTHORITY_FIELD)
    private Authority authority;
    private UserCredentials userCredentials;
    private String defaultAddressId;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserEntity [id=");
        builder.append(this.getId());
        builder.append(", name=");
        builder.append(this.getName());
        builder.append(", email=");
        builder.append(this.getEmail());
        builder.append(", authority");
        builder.append(this.getAuthority().name());
        builder.append(", createdAt=");
        builder.append(this.getCreatedAt());
        builder.append(", updatedAt=");
        builder.append(this.getUpdatedAt());
        builder.append(", defaultAddressId=");
        builder.append(this.getDefaultAddressId());
        builder.append(", userCredentials=");
        builder.append(this.getUserCredentials().toString());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public User toData() {
        User user = new User();
        user.setId(this.getId());
        user.setName(this.getName());
        user.setEmail(this.getEmail());
        user.setAuthority(this.getAuthority());
        user.setCreatedAt(this.getCreatedAt());
        user.setUpdatedAt(this.getUpdatedAt());
        user.setUserCredentials(this.getUserCredentials());
        user.setDefaultAddressId(this.getDefaultAddressId());
        return user;
    }

}
