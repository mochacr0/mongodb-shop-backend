package com.example.springbootmongodb.security;

import com.example.springbootmongodb.exception.AuthMethodNotSupportedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtRefreshTokenProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;
    private final AuthenticationDetailsSource<HttpServletRequest, RestAuthenticationDetails> authenticationDetailsSource = new RestAuthenticationDetailsSource();

    public JwtRefreshTokenProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler, ObjectMapper objectMapper) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthMethodNotSupportedException("Authentication method not supported: " + request.getMethod());
        }
        RefreshTokenRequest refreshTokenRequest;
        try {
            refreshTokenRequest = objectMapper.readValue(request.getReader(), RefreshTokenRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid refresh token request payload");
        }
        if (StringUtils.isBlank(refreshTokenRequest.getRefreshToken())) {
            throw new AuthenticationServiceException("Refresh token is not provided");
        }
        JwtRefreshAuthenticationToken token = new JwtRefreshAuthenticationToken(refreshTokenRequest.getRefreshToken());
        token.setDetails(authenticationDetailsSource.buildDetails(request));
        return super.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
