package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAddressRequest {
    @Schema(description = "Tên nguười nhận hàng", example = "Nguyen Van A")
    private String name;
    @Schema(description = "Số điện thoại của người nhận hàng", example = "0123456789")
    private String phoneNumber;
    @Schema(description = "Tỉnh", example = "0123456789")
    private String province;
    @Schema(description = "Quận", example = "0123456789")
    private String district;
    @Schema(description = "Phường", example = "0123456789")
    private String ward;
    @Schema(description = "Đường, số nhà", example = "0123456789")
    private String streetAddress;
    private boolean isDefault;

    @Schema(description = "Địa chỉ này có phải là địa chỉ mặc định hay không", example = "false")
    public boolean isDefault() {
        return isDefault;
    }
}
