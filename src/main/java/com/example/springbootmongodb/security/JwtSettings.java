package com.example.springbootmongodb.security;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@ConfigurationProperties(prefix = "jwt.settings")
@Data
public class JwtSettings {
    private Integer accessTokenExpiryTimeSeconds;
    private Integer refreshTokenExpiryTimeSeconds;
    private String tokenIssuer;
    private String tokenSigningKeyString;
    private Key signingKey;

    public Key getTokenSigningKey() {
        if (StringUtils.isEmpty(this.tokenSigningKeyString)) {
            return null;
        }
        if (this.signingKey == null) {
            this.signingKey = Keys.hmacShaKeyFor(this.tokenSigningKeyString.getBytes(StandardCharsets.UTF_8));
        }
        return this.signingKey;
    }
}
