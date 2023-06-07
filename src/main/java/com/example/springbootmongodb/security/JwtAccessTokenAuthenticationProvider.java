package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtTokenFactory tokenFactory;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String unsafeToken = (String)authentication.getCredentials();
        SecurityUser securityUser = tokenFactory.parseJwtAccessToken(unsafeToken);
        return new JwtAccessAuthenticationToken(securityUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAccessAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
