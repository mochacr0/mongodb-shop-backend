package com.example.springbootmongodb.common.data.shipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShipmentRequest {
    @Schema(description = "Id địa chỉ nhận hàng trả về. (Nếu trường này null sẽ dùng địa chỉ gửi)")
    private String returnAddressId;
    @Schema(description = "Phương thức giao hàng cho đơn vị vận chuyển. (cod:  Shipper tới lấy hàng, post: Shop tự mang hàng đến bưu cục)", example = "cod")
    private String pickOption;
    @Schema(description = "Chọn ca lấy hàng (morning, afternoon, night)", example = "morning")
    private String pickWorkShipOption;
    @Schema(description = "Chọn ca giao hàng (morning, afternoon, night)", example = "morning")
    private String deliverWorkShipOption;
    @Schema(description = "Ghi chú cho đơn vị vận chuyển", example = "Hàng dễ vỡ, nhẹ tay")
    private String note;
//    @Builder.Default
//    private boolean isFreeShip = false;
//
//    @Schema(name = "isFeeShip", description = "Có miễn phí giao hàng cho người mua không", example = "false")
//    @JsonProperty
//    public boolean isFreeShip() {
//        return isFreeShip;
//    }
}
