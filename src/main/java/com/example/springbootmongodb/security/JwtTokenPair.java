package com.example.springbootmongodb.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenPair {
    private String accessToken;
    private String refreshToken;
}
