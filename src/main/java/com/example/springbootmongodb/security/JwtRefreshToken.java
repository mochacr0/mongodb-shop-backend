package com.example.springbootmongodb.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtRefreshToken implements JwtToken {
    @JsonProperty(value = "refreshToken")
    private String value;

    @Override
    public String getValue() {
        return this.value;
    }
    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
