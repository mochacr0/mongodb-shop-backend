package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GHTKOrderResponse {
    @JsonAlias("partner_id")
    private String partnerId;
    private String label;
    private String area;
    private int fee;
    @JsonAlias("insurance_fee")
    private int insuranceFee;
    @JsonAlias("estimated_pick_time")
    private String estimatedPickTime;
    @JsonAlias("estimated_deliver_time")
    private String estimatedDeliverTime;
    List<Object> products = new ArrayList<>();
    @JsonAlias("status_id")
    private int statusId;
}
