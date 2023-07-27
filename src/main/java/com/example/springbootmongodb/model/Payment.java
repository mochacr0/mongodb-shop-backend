package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {
    private String requestId;
    private String transId;
    private boolean isPaid;
    private long amount;
    private PaymentMethod method;
}
