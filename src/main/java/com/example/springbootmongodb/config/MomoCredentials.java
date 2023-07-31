package com.example.springbootmongodb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.momo")
@Getter
@Setter
public class MomoCredentials {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
}
