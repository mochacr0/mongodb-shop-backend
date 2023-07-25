package com.example.springbootmongodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RestServiceConfiguration {
    @Bean
    public HttpClient buildHttpClient() {
        return HttpClient
                .newBuilder()
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
    }
}
