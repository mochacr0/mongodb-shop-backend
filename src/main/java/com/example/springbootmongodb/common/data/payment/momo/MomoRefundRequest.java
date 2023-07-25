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
public class MomoRefundRequest extends MomoAbstractRequest {
    private String subPartnerCode;
    private long amount;
    private String transId;
    private String description;
}
