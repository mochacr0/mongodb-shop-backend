package com.example.springbootmongodb.common.data.payment.momo;

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
public class MomoAbstractPayload {
    protected String partnerCode;
    protected String requestId;
    protected String orderId;
    protected String signature;
}
