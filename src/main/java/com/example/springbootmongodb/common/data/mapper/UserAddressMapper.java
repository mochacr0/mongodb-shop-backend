package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UserAddressMapper {
    @Autowired
    @Lazy
    private UserService userService;
    public UserAddressEntity toEntity(UserAddress userAddress) {
        return UserAddressEntity
                .builder()
                .userId(userAddress.getUserId())
                .name(userAddress.getName())
                .province(userAddress.getProvince())
                .district(userAddress.getDistrict())
                .ward(userAddress.getWard())
                .streetAddress(userAddress.getStreetAddress())
                .build();
    }

    public UserAddress fromEntity(UserAddressEntity entity) {
        return UserAddress.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .province(entity.getProvince())
                .district(entity.getDistrict())
                .ward(entity.getWard())
                .streetAddress(entity.getStreetAddress())
                .build();
    }

    public List<UserAddress> toUserAddressList(List<UserAddressEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        UserEntity user = userService.findById(entities.get(0).getUserId());
        List<UserAddress> userAddressList = new ArrayList<>();
        for (UserAddressEntity entity : entities) {
            UserAddress userAddress = this.fromEntity(entity);
            if (userAddress.getId().equals(user.getDefaultAddressId())) {
                userAddress.setDefault(true);
            }
            userAddressList.add(userAddress);
        }
        return userAddressList;
    }

    public void updateFields(UserAddressEntity entity, UserAddress address) {
        entity.setName(address.getName());
        entity.setProvince(address.getProvince());
        entity.setDistrict(address.getDistrict());
        entity.setWard(address.getWard());
        entity.setStreetAddress(address.getStreetAddress());
    }
}