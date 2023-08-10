package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class GHTKUpdateStatusRequest {
//    @JsonProperty("label_id")
    private String shipmentId;
//    @JsonProperty("partner_id")
    private String partnerId;
//    @JsonProperty("status_id")
    private Integer statusId;
//    @JsonProperty("action_time")
    private String actionTime;
//    @JsonProperty("reason_code")
    private String reasonCode;
    private String reason;
    private Float weight;
    private Integer fee;
//    @JsonProperty("return_part_package")
    private Integer returnPartPackage;
}
