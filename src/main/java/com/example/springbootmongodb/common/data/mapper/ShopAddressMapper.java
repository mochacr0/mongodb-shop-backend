package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.ShopAddressRequest;
import com.example.springbootmongodb.model.ShopAddressEntity;
import com.nimbusds.openid.connect.sdk.op.EndpointName;
import org.springframework.stereotype.Component;

@Component
public class ShopAddressMapper {
    public ShopAddressEntity toEntity(ShopAddressRequest request) {
        return ShopAddressEntity
                .builder()
                .phoneNumber(request.getPhoneNumber())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .streetAddress(request.getStreetAddress())
                .isDefault(request.isDefault())
                .build();
    }

    public ShopAddress fromEntity(ShopAddressEntity entity) {
        return ShopAddress
                .builder()
                .id(entity.getId())
                .phoneNumber(entity.getPhoneNumber())
                .province(entity.getProvince())
                .district(entity.getDistrict())
                .ward(entity.getWard())
                .streetAddress(entity.getStreetAddress())
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateFields(ShopAddressEntity entity, ShopAddressRequest request) {
        entity.setPhoneNumber(entity.getPhoneNumber());
        entity.setProvince(request.getProvince());
        entity.setDistrict(request.getDistrict());
        entity.setWard(request.getWard());
        entity.setStreetAddress(request.getStreetAddress());
        entity.setDefault(request.isDefault());
    }
}
