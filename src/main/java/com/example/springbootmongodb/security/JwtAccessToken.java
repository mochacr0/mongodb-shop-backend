package com.example.springbootmongodb.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class JwtAccessToken implements JwtToken {
    @JsonProperty(value = "accessToken")
    private String value;

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
