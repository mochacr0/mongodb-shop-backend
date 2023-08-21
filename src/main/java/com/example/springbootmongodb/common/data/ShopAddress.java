package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.HasAddress;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ShopAddress extends AbstractData implements HasAddress  {
    @Schema(description = "Tên người giao hàng cho đơn vị vận chuyển", example = "0123456789")
    private String name;
    @Schema(description = "Số điện thoại của shop", example = "0123456789")
    private String phoneNumber;
    @Schema(description = "Tỉnh", example = "Tỉnh 1")
    private String province;
    @Schema(description = "Quận", example = "Quận 1")
    private String district;
    @Schema(description = "Phường", example = "Phường 1")
    private String ward;
    @Schema(description = "Thôn/ấp/xóm/tổ", example = "Tổ 1")
    private String hamlet;
    @Schema(description = "Đường ", example = "Đường 1")
    private String street;
    @Schema(description = "Địa chỉ chi tiết", example = "Số 1")
    private String addressDetails;
    @Schema(name = "isDefault", description = "Địa chỉ này có phải là địa chỉ mặc định của shop hay không", example = "false")
    @JsonProperty(value = "isDefault")
    private boolean isDefault;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopAddress that = (ShopAddress) o;
        return isDefault() == that.isDefault()
                && Objects.equals(getPhoneNumber(), that.getPhoneNumber())
                && Objects.equals(getProvince(), that.getProvince())
                && Objects.equals(getDistrict(), that.getDistrict())
                && Objects.equals(getWard(), that.getWard())
                && Objects.equals(getHamlet(), that.getHamlet())
                && Objects.equals(getStreet(), that.getStreet())
                && Objects.equals(getAddressDetails(), that.getAddressDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPhoneNumber(), getProvince(), getDistrict(), getWard(), getHamlet(), getStreet(), getAddressDetails(), isDefault());
    }
}
