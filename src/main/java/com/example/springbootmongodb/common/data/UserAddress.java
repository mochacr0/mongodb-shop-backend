package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ToEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserAddress extends AbstractData implements ToEntity<UserAddressEntity> {
    private String userId;
    private String name;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String streetAddress;
    private boolean isDefault;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserAddress [id=");
        builder.append(this.getId());
        builder.append(", userId");
        builder.append(this.getUserId());
        builder.append(", name");
        builder.append(this.getName());
        builder.append(", phoneNumber");
        builder.append(this.getPhoneNumber());
        builder.append(", province");
        builder.append(this.getProvince());
        builder.append(", district");
        builder.append(this.getDistrict());
        builder.append(", ward");
        builder.append(this.getWard());
        builder.append(", streetAddress");
        builder.append(this.getStreetAddress());
        builder.append(", isDefault");
        builder.append(this.isDefault());
        return builder.toString();
    }

    @Override
    public UserAddressEntity toEntity() {
        UserAddressEntity entity = new UserAddressEntity();
        entity.setId(this.getId());
        entity.setUserId(this.getUserId());
        entity.setName(this.getName());
        entity.setProvince(this.getProvince());
        entity.setDistrict(this.getDistrict());
        entity.setWard(this.getWard());
        entity.setStreetAddress(this.getStreetAddress());
        return entity;
    }
}
