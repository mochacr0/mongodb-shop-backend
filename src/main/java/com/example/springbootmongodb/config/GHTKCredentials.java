package com.example.springbootmongodb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "shipping.ghtk")
@Getter
@Setter
public class GHTKCredentials {
    private String apiToken;
}
