package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Length;
import com.example.springbootmongodb.model.ToEntity;
import com.example.springbootmongodb.model.UserCredentials;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.security.Authority;
import com.example.springbootmongodb.security.oauth2.OAuth2UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.example.springbootmongodb.model.ModelConstants.USER_EMAIL_FIELD;
import static com.example.springbootmongodb.model.ModelConstants.NAME_FIELD;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema
public class User extends AbstractData implements ToEntity<UserEntity> {
    @Schema(title = "name", description = "Username", example = "user00")
    @Length(fieldName = NAME_FIELD)
    private String name;
    @Schema(title = "email", description = "User email", example = "nthai2001cr@gmail.com")
    @Length(fieldName = USER_EMAIL_FIELD)
    @Email
    private String email;
    @Schema(title = "role", description = "User role", example = "USER/ADMIN")
    private Authority authority = Authority.USER;
    @JsonIgnore
    private UserCredentials userCredentials;
    @Schema(title = "defaultAddressId", description = "User default address ID", example = "647d222a59a4582894a95c10")
    private String defaultAddressId;

    @Override
    @Schema(title = "id", description = "User ID", example = "647d222a59a4582894a95c10")
    public String getId() {
        return this.id;
    }

    @Override
    @Schema(title = "createdAt", description = "Timestamp of the user creation", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    @Schema(title = "updatedAt", description = "Timestamp of the user update", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Authority getAuthority() {
        return this.authority;
    }

    public User(User user) {
        this.setId(user.getId());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setAuthority(user.getAuthority());
        this.setCreatedAt(user.getCreatedAt());
        this.setUpdatedAt(user.getUpdatedAt());
        this.setUserCredentials(user.getUserCredentials());
    }

    public User(OAuth2UserInfo oauth2UserInfo) {
        this.setName(oauth2UserInfo.getName());
        this.setEmail(oauth2UserInfo.getEmail());
    }

    public User(RegisterUserRequest registerUserRequest) {
        this.setName(registerUserRequest.getName());
        this.setEmail(registerUserRequest.getEmail());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [id=");
        builder.append(this.id);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", email=");
        builder.append(this.email);
        builder.append(", authority=");
        builder.append(this.authority.name());
        builder.append(", defaultAddressId=");
        builder.append(this.defaultAddressId);
        builder.append(", createdAt=");
        builder.append(this.createdAt);
        builder.append(", updatedAt=");
        builder.append(this.updatedAt);
        builder.append(", userCredentials");
        builder.append(this.getUserCredentials());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public UserEntity toEntity() {
        return UserEntity
                .builder()
                .id(this.getId())
                .name(this.getName())
                .email(this.getEmail())
                .authority(this.getAuthority())
                .userCredentials(this.getUserCredentials())
                .defaultAddressId(this.getDefaultAddressId())
                .build();
    }

    public static User fromEntity(UserEntity entity) {
        return builder()
                .id(entity.getId())
                .defaultAddressId(entity.getDefaultAddressId())
                .name(entity.getName())
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .userCredentials(entity.getUserCredentials())
                .build();
    }

    //        User user = new User();
//        user.setId(this.getId());
//        user.setName(this.getName());
//        user.setEmail(this.getEmail());
//        user.setAuthority(this.getAuthority());
//        user.setCreatedAt(this.getCreatedAt());
//        user.setUpdatedAt(this.getUpdatedAt());
//        user.setUserCredentials(this.getUserCredentials());
//        user.setDefaultAddressId(this.getDefaultAddressId());
}
