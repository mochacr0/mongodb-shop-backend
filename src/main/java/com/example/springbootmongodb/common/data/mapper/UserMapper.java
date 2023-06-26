package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.security.Authority;
import com.example.springbootmongodb.security.oauth2.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .authority(user.getAuthority())
                .userCredentials(user.getUserCredentials())
                .defaultAddressId(user.getDefaultAddressId())
                .build();
    }

    public UserEntity toEntity(RegisterUserRequest request) {
        return UserEntity
                .builder()
                .name(request.getName())
                .email(request.getEmail())
                .authority(Authority.USER)
                .build();
    }

    public UserEntity toEntity(OAuth2UserInfo oAuth2UserInfo) {
        return UserEntity
                .builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .build();
    }

    public User toUser(UserEntity entity) {
        return User.builder()
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
}
