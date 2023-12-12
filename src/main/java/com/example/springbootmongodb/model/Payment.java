package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.payment.PaymentStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {
    private String transId;
    private long amount;
    private long refundableAmount;
    private String payUrl;
    private PaymentMethod method;
    private PaymentStatus status;
    private String description;
    private String currentRequestId;
}
