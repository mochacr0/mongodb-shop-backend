package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.security.SecurityUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public abstract class AbstractJwtAuthenticationToken extends AbstractAuthenticationToken {
    private String unsafeToken;
    private SecurityUser securityUser;

    protected AbstractJwtAuthenticationToken(String unsafeToken) {
        super(null);
        this.unsafeToken = unsafeToken;
    }

    protected AbstractJwtAuthenticationToken(SecurityUser securityUser) {
        super(securityUser.getAuthorities());
        this.securityUser = securityUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.unsafeToken;
    }

    @Override
    public Object getPrincipal() {
        return this.securityUser;
    }
}
