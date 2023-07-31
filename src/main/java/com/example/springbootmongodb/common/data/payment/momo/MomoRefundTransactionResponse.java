package com.example.springbootmongodb.common.data.payment.momo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MomoRefundTransactionResponse {
    private String orderId;
    private long amount;
    private int resultCode;
    private String transId;
    private long createdTime;
}
