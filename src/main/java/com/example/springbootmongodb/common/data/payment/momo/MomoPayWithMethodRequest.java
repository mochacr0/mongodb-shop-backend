package com.example.springbootmongodb.common.data.payment.momo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class MomoPayWithMethodRequest extends MomoAbstractRequest {
    private String partnerName;
    private String storeId;
    private long amount;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    //items;
    private List<MomoPayWithMethodItem> items = new ArrayList<>();
    //userInfo;
    private MomoUserInfo userInfo;
    @JsonProperty("orderExpireTime")
    private int orderExpireTimeInMinute;
    private boolean autoCapture;
}
