package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserSimplification;
import com.example.springbootmongodb.common.data.payment.momo.MomoUserInfo;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.security.Authority;
import com.example.springbootmongodb.security.oauth2.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
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

    public User fromEntity(UserEntity entity) {
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

    public MomoUserInfo fromEntityToMomoUserInfo(UserEntity entity) {
        return MomoUserInfo
                .builder()
                .email(entity.getEmail())
                .name(entity.getName())
                .build();
    }

    public UserSimplification fromEntityToUserSimplification(UserEntity entity) {
        return UserSimplification
                .builder()
                .id(entity.getEmail())
                .name(entity.getName())
                .build();
    }
}
