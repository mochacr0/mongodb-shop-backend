package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKFeeResponse {
    private String name;
    private int fee;
    @JsonProperty("insurance_fee")
    private int insuranceFee;
    private String a;
    private String dt;
    private List<GHTKExtensionFee> extFees = new ArrayList<>();
    @JsonProperty("delivery")
    private boolean isAddressSupported;
}
