package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenFactory tokenFactory;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String unsafeToken = (String)authentication.getCredentials();
        SecurityUser securityUser = tokenFactory.parseJwtAccessToken(unsafeToken);
        try {
            userService.findById(securityUser.getId());
        } catch (InvalidDataException | ItemNotFoundException exception) {
            throw new AuthenticationServiceException("Invalid access token: " + exception.getMessage());
        }
        return new JwtAccessAuthenticationToken(securityUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAccessAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
