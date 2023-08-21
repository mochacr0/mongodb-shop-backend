package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.ShopAddressRequest;
import com.example.springbootmongodb.model.ShopAddressEntity;
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
                .hamlet(request.getHamlet())
                .street(request.getStreet())
                .addressDetails(request.getAddress())
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
                .hamlet(entity.getHamlet())
                .street(entity.getStreet())
                .addressDetails(entity.getAddressDetails())
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
        entity.setHamlet(request.getHamlet());
        entity.setStreet(request.getStreet());
        entity.setAddressDetails(request.getAddress());
        entity.setDefault(request.isDefault());
    }

}
