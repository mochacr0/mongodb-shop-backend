package com.example.springbootmongodb.common.data.shipment;

import com.example.springbootmongodb.common.HasAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShipmentAddress implements HasAddress {
    @Schema(description = "Tên người giao hàng cho đơn vị vận chuyển", example = "Nguyễn Văn A")
    private String name;
    @Schema(description = "Số điện thoại", example = "0123456789")
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
}
