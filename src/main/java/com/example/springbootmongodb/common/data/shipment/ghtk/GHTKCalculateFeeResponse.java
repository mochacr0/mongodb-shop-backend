package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GHTKCalculateFeeResponse extends GHTKAbstractResponse {
    private GHTKFeeResponse fee;
}
