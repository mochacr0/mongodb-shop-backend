package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShopAddressRequest {
    @Schema(description = "Id của địa chỉ", example = "647d222a59a4582894a95c10")
    private String id;
    @Schema(description = "Số điện thoại của chủ cửa hàng", example = "0123456789")
    private String phoneNumber;
    @Schema(description = "Tỉnh/Thành", example = "0123456789")
    private String province;
    @Schema(description = "Quận/Huyện", example = "0123456789")
    private String district;
    @Schema(description = "Phường/Xã", example = "0123456789")
    private String ward;
    @Schema(description = "Đường, số nhà", example = "0123456789")
    private String streetAddress;
    private boolean isDefault;

    @Schema(description = "Địa chỉ này có phải là địa chỉ mặc định hay không", example = "false")
    public boolean isDefault() {
        return isDefault;
    }
}
