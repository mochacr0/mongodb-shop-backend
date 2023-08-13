package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.service.UserService;
import io.micrometer.common.util.StringUtils;
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
                .phoneNumber(userAddress.getPhoneNumber())
                .province(userAddress.getProvince())
                .district(userAddress.getDistrict())
                .ward(userAddress.getWard())
                .hamlet(StringUtils.isEmpty(userAddress.getHamlet()) ? "Khác" : userAddress.getHamlet())
                .street(userAddress.getStreet())
                .addressDetails(userAddress.getAddressDetails())
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
                .hamlet(entity.getHamlet())
                .ward(entity.getWard())
                .street(entity.getStreet())
                .addressDetails(entity.getAddressDetails())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
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
        entity.setHamlet(address.getHamlet());
        entity.setStreet(address.getStreet());
        entity.setAddressDetails(address.getAddressDetails());
    }
}