package com.example.springbootmongodb.security;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    String refreshToken;
}
