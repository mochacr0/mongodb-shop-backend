package com.example.springbootmongodb.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class MomoCredentials {
    private String partnerCode = "MOMO";
    private String accessKey = "F8BBA842ECF85";
    private String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
}
