package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.example.springbootmongodb.common.data.ShopAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GHTKCalculateFeeResponse extends GHTKAbstractResponse {
    @Schema(description = "Phí vận chuyển")
    private GHTKFeeResponse fee;
    @Schema(description = "Địa chỉ của shop được dùng để tính phí")
    private ShopAddress shopAddress;
}
