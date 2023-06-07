package com.example.springbootmongodb.security;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenExtractor {
    private static final String BEARER_AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public String extract(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isBlank(header)) {
            throw new AuthenticationServiceException("Unauthorized");
        }
        if (header.length() <= BEARER_AUTHORIZATION_HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid Authorization header length");
        }
        return header.substring(BEARER_AUTHORIZATION_HEADER_PREFIX.length(), header.length());
    }
}
