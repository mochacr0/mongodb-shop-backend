package com.example.springbootmongodb.common.data.payment.momo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MomoPayWithMethodResponse extends MomoAbstractResponse {
    private String payUrl;
    private String shortLink;
}
