package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.mapper.UserMapper;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserCredentials;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class JwtRefreshTokenAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenFactory jwtTokenFactory;
    private final  UserService userService;
    private final UserMapper mapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        JwtRefreshAuthenticationToken unsafeToken = (JwtRefreshAuthenticationToken)authentication;
        SecurityUser unsafeUser = jwtTokenFactory.parseJwtRefreshToken((String)unsafeToken.getCredentials());
        UserEntity user;
        try {
            user = userService.findByName(unsafeUser.getName());
        } catch (ItemNotFoundException ex) {
            throw new AuthenticationServiceException("Cannot find user with given refresh token");
        }
        if (user.getAuthority() == null) {
            throw new InsufficientAuthenticationException("User has no authority assigned");
        }
        UserCredentials userCredentials = user.getUserCredentials();
        if (userCredentials == null) {
            throw new AuthenticationServiceException("Current user does not have user credentials");
        }
        //check email verified && check current lock by IP
        RestAuthenticationDetails authenticationDetails = (RestAuthenticationDetails) authentication.getDetails();
        if (!(userCredentials.isVerified() && userCredentials.isEnabled(authenticationDetails.getClientIpAddress()))) {
            throw new DisabledException("User is not active");
        }
        SecurityUser securityUser = new SecurityUser(mapper.toUser(user));
        return new JwtAccessAuthenticationToken(securityUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtRefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
