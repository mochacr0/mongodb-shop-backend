package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAddressRequest {
    @Required(fieldName = "User name")
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
    @Schema(description = "Thôn/ấp/xóm/tổ", example = "Tổ 1")
    private String hamlet;
    @Required(fieldName = "Street name")
    @Schema(description = "Đường", example = "Đường 1")
    private String street;
    @Required(fieldName = "Detail address")
    @Schema(description = "Địa chỉ chi tiết", example = "Số 1")
    private String address;
    private boolean isDefault;

    @Schema(name = "isDefault", description = "Địa chỉ này có phải là địa chỉ mặc định hay không", example = "false")
    @JsonProperty(value = "isDefault")
    public boolean isDefault() {
        return isDefault;
    }
}
