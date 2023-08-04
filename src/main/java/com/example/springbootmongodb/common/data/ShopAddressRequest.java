package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
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
    @Required(fieldName = "Phone number")
    @Schema(description = "Số điện thoại của chủ cửa hàng", example = "0123456789")
    private String phoneNumber;
    @Required(fieldName = "Province")
    @Schema(description = "Tỉnh/Thành", example = "Tỉnh 1")
    private String province;
    @Required(fieldName = "District")
    @Schema(description = "Quận/Huyện", example = "Quận 1")
    private String district;
    @Required(fieldName = "Ward")
    @Schema(description = "Phường/Xã", example = "Phường 1")
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

    @Schema(description = "Địa chỉ này có phải là địa chỉ mặc định hay không", example = "false")
    public boolean isDefault() {
        return isDefault;
    }
}
