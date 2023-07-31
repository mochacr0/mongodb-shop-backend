package com.example.springbootmongodb.common.data.payment.momo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class MomoQueryPaymentStatusResponse extends MomoAbstractResponse {
    private String extraData;
    private String payType;
    private String transId;
    private List<MomoRefundTransactionResponse> refundTrans;
}
