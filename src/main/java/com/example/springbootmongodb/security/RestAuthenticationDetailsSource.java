package com.example.springbootmongodb.security;

import com.example.springbootmongodb.security.RestAuthenticationDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class RestAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, RestAuthenticationDetails> {
    @Override
    public RestAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new RestAuthenticationDetails(context);
    }
}
