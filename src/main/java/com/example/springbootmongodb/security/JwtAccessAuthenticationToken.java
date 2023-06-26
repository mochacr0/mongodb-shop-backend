package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.security.SecurityUser;

public class JwtAccessAuthenticationToken extends AbstractJwtAuthenticationToken{
    public JwtAccessAuthenticationToken(String unsafeToken) {
        super(unsafeToken);
    }
    public JwtAccessAuthenticationToken(SecurityUser securityUser) {
        super(securityUser);
    }
}
