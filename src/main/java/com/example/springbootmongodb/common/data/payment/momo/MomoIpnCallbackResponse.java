package com.example.springbootmongodb.common.data.payment.momo;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class MomoIpnCallbackResponse extends MomoAbstractResponse {
    private String extraData;
    private String orderInfo;
    private String orderType;
    private String payType;
    private String transId;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MomoIpnRequest [partnerCode=");
        builder.append(this.getPartnerCode());
        builder.append(", orderId=");
        builder.append(this.getOrderId());
        builder.append(", requestId=");
        builder.append(this.getRequestId());
        builder.append(", amount=");
        builder.append(this.getAmount());
        builder.append(", orderInfo=");
        builder.append(this.getOrderInfo());
        builder.append(", orderType=");
        builder.append(this.getOrderType());
        builder.append(", transId=");
        builder.append(this.getTransId());
        builder.append(", resultCode=");
        builder.append(this.getResultCode());
        builder.append(", message=");
        builder.append(this.getMessage());
        builder.append(", payType=");
        builder.append(this.getPayType());
        builder.append(", extraData=");
        builder.append(this.getExtraData());
        builder.append(", signature=");
        builder.append(this.getSignature());
        builder.append("]");
        return builder.toString();
    }

}
