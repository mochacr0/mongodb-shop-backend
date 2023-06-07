package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.security.SecurityUser;

public class JwtRefreshAuthenticationToken extends AbstractJwtAuthenticationToken{
    public JwtRefreshAuthenticationToken(String unsafeToken) {
        super(unsafeToken);
    }

    public JwtRefreshAuthenticationToken(SecurityUser securityUser) {
        super(securityUser);
    }
}
