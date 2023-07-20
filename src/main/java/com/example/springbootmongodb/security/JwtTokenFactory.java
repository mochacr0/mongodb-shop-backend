package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.ExpiredJwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFactory {
    private final JwtSettings jwtSettings;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final String SCOPES = "scopes";
    private final String USER_ID = "userId";

    public JwtTokenPair createJwtTokenPair(SecurityUser securityUser) {
        JwtToken accessToken = createAccessToken(securityUser);
        JwtToken refreshToken = createRefreshToken(securityUser);
        return new JwtTokenPair(accessToken.getValue(), refreshToken.getValue());
    }

    public JwtToken createAccessToken(SecurityUser securityUser) {
        List<String> scopes = securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        JwtBuilder jwtBuilder = setUpToken(securityUser, jwtSettings.getAccessTokenExpiryTimeSeconds(), scopes);
        String jwtToken = jwtBuilder.compact();
        return new JwtAccessToken(jwtToken);
    }

    public JwtToken createRefreshToken(SecurityUser securityUser) {
        List<String> scopes = Collections.singletonList(Authority.REFRESH_TOKEN.name());
        JwtBuilder jwtBuilder = setUpToken(securityUser, jwtSettings.getRefreshTokenExpiryTimeSeconds(), scopes);
        String jwtToken = jwtBuilder.compact();
        return new JwtAccessToken(jwtToken);
    }

    private JwtBuilder setUpToken(SecurityUser securityUser, long tokenExpiryTime, List<String> scopes) {
        if (StringUtils.isEmpty(securityUser.getName())) {
            throw new IllegalArgumentException("Cannot create JWT token with empty username");
        }
        //scopes ?
        Claims claims = Jwts.claims().setSubject(securityUser.getName());
        claims.put(SCOPES, scopes);
        claims.put(USER_ID, securityUser.getId());
        ZonedDateTime currentTime = ZonedDateTime.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(currentTime.plusSeconds(tokenExpiryTime).toInstant()))
                .signWith(jwtSettings.getTokenSigningKey(), SignatureAlgorithm.HS256);
    }

    public SecurityUser parseJwtAccessToken(String jwtAccessToken) {
        Claims claims = parseJwtTokenClaims(jwtAccessToken);
        String subject = claims.getSubject();
        List<String> scopes = claims.get(SCOPES, List.class);
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("Access token doesn't have any scopes");
        }
        String userId = claims.get(USER_ID, String.class);
        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(userId);
        securityUser.setName(subject);
        securityUser.setAuthority(Authority.parseFromString(scopes.get(0)));
        return securityUser;
    }

    public SecurityUser parseJwtRefreshToken(String jwtRefreshToken) {
        Claims claims = parseJwtTokenClaims(jwtRefreshToken);
        String subject = claims.getSubject();
        List<String> scopes = claims.get(SCOPES, List.class);
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("Access token doesn't have any scopes");
        }
        if (!scopes.get(0).equals(Authority.REFRESH_TOKEN.name())) {
            throw new IllegalArgumentException("Invalid refresh token scope");
        }
        String userId = claims.get(USER_ID, String.class);
        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(userId);
        securityUser.setName(subject);
        securityUser.setAuthority(Authority.parseFromString(scopes.get(0)));
        return securityUser;
    }

    private Claims parseJwtTokenClaims(String jwtToken) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(this.jwtSettings.getTokenSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        }
        catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            throw new BadCredentialsException("Invalid JWT token: " + exception.getMessage());
        }
        catch (ExpiredJwtException exception) {
            throw new ExpiredJwtTokenException("Expired JWT token");
        }
    }

}
