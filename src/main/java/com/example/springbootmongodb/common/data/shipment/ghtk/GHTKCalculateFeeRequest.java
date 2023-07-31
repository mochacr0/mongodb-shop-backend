package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKCalculateFeeRequest {
    @JsonProperty("pick_address_id")
    private String pickAddressId;
    @JsonProperty("pick_address")
    private String pickAddress;
    @JsonProperty("pick_province")
    private String pickProvince;
    @JsonProperty("pick_district")
    private String pickDistrict;
    @JsonProperty("pick_ward")
    private String pickWard;
    @JsonProperty("pick_street")
    private String pickStreet;
    private String address;
    private String province;
    private String district;
    private String ward;
    private String street;
    private int weight;
    private String value;
    private String transport;
    @JsonProperty("deliver_option")
    private String deliverOption = "none";
    private List<Integer> tags = new ArrayList<>();
}
