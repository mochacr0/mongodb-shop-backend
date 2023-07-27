package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class PaymentEntity {
    private String transId;
    private PaymentMethod method;
    private boolean isPaid;
    private long amount;
}
