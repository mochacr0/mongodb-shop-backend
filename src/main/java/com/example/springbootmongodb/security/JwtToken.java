package com.example.springbootmongodb.security;

public interface JwtToken {
    public String getValue();
    public void setValue(String value);
}
