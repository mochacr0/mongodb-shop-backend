package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ToEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserAddress extends AbstractData implements ToEntity<UserAddressEntity> {
    @Schema(title = "userId", description = "User ID", example = "647d222a59a4582894a95c10")
    private String userId;
    @Schema(title = "name", description = "Receiver name", example = "Nguyen Van A")
    private String name;
    @Schema(title = "phoneNumber", description = "Receiver phone number", example = "0123456789")
    private String phoneNumber;
    @Schema(title = "province", description = "Delivery province", example = "0123456789")
    private String province;
    @Schema(title = "district", description = "Delivery district", example = "0123456789")
    private String district;
    @Schema(title = "ward", description = "Delivery ward", example = "0123456789")
    private String ward;
    @Schema(title = "streetAddress", description = "Delivery street ", example = "0123456789")
    private String streetAddress;
    @Schema(title = "isDefault", description = "If this email address is the default for the user, this boolean value will be true", example = "false")
    private boolean isDefault;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserAddress [id=");
        builder.append(this.getId());
        builder.append(", userId=");
        builder.append(this.getUserId());
        builder.append(", name=");
        builder.append(this.getName());
        builder.append(", phoneNumber=");
        builder.append(this.getPhoneNumber());
        builder.append(", province=");
        builder.append(this.getProvince());
        builder.append(", district=");
        builder.append(this.getDistrict());
        builder.append(", ward=");
        builder.append(this.getWard());
        builder.append(", streetAddress=");
        builder.append(this.getStreetAddress());
        builder.append(", isDefault=");
        builder.append(this.isDefault());
        return builder.toString();
    }

    @Override
    public UserAddressEntity toEntity() {
        return UserAddressEntity
                .builder()
                .userId(this.getUserId())
                .name(this.getName())
                .province(this.getProvince())
                .district(this.getDistrict())
                .ward(this.getWard())
                .streetAddress(this.getStreetAddress())
                .build();
    }

    public static UserAddress fromEntity(UserAddressEntity entity) {
        return builder()
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

}
