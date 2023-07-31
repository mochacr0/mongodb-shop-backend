package com.example.springbootmongodb.common.data.payment.momo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class MomoCaptureWalletRequest extends MomoAbstractRequest {
    private String subPartnerCode;
    private String storeName;
    private String storeId;
    private long amount;
    private String orderInfo;
    private String orderGroupId;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    private List<MomoItem> items = new ArrayList<>();
    private MomoDeliveryInfo deliveryInfo;
    private MomoUserInfo userInfo;
    private String referenceId;
    private boolean autoCapture;
}
