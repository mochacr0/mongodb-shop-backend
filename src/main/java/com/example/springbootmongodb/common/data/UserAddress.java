package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
import com.example.springbootmongodb.model.ToEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserAddress extends AbstractData implements ToEntity<UserAddressEntity> {
    @Schema(description = "User Id", example = "647d222a59a4582894a95c10")
    private String userId;
    @Required(fieldName = "Receiver name")
    @Schema(description = "Tên nguười nhận hàng", example = "Nguyen Van A")
    private String name;
    @Required(fieldName = "Phone number")
    @Schema(description = "Số điện thoại của người nhận hàng", example = "0123456789")
    private String phoneNumber;
    @Required(fieldName = "Province")
    @Schema(description = "Tỉnh", example = "Tỉnh 1")
    private String province;
    @Required(fieldName = "District")
    @Schema(description = "Quận", example = "Quận 1")
    private String district;
    @Required(fieldName = "Ward")
    @Schema(description = "Phường", example = "Phường 1")
    private String ward;
    @Required(fieldName = "Street name")
    @Schema(description = "Đường ", example = "Đường 1")
    private String street;
    @Required(fieldName = "Detail address")
    @Schema(description = "Địa chỉ chi tiết", example = "Số 1")
    private String address;
    @Schema(description = "Thôn/ấp/xóm/tổ", example = "Tổ 1")
    private String hamlet;
    private boolean isDefault;

    @Schema(name = "isDefault", description = "Địa chỉ này có phải là địa chỉ mặc định hay không", example = "false")
    @JsonProperty(value = "isDefault")
    public boolean isDefault() {
        return isDefault;
    }

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
        builder.append(", hamlet=");
        builder.append(this.getHamlet());
        builder.append(", street=");
        builder.append(this.getStreet());
        builder.append(", address=");
        builder.append(this.getAddress());
        builder.append(", isDefault=");
        builder.append(this.isDefault());
        builder.append("]");
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
                .street(this.getStreet())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAddress that = (UserAddress) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getUserId(), that.getUserId())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getPhoneNumber(), that.getPhoneNumber())
                && Objects.equals(getProvince(), that.getProvince())
                && Objects.equals(getDistrict(), that.getDistrict())
                && Objects.equals(getHamlet(), that.getHamlet())
                && Objects.equals(getWard(), that.getWard())
                && Objects.equals(getStreet(), that.getStreet())
                && Objects.equals(getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getName(), getPhoneNumber(), getProvince(), getDistrict(), getWard(), getHamlet(), getStreet(), getAddress(), isDefault());
    }
}
