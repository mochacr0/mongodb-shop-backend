package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.data.mapper.UserMapper;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.service.UserCredentialsService;
import com.example.springbootmongodb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final UserCredentialsService userCredentialsService;
    private final UserMapper mapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication provided");
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        RestAuthenticationDetails details = (RestAuthenticationDetails) authentication.getDetails();
        UserEntity existingUser;
        try {
            existingUser = userService.findByName(username);
        }
        catch (ItemNotFoundException ex) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        //authenticate with password
        if (existingUser.getUserCredentials() == null) {
            throw new UsernameNotFoundException("User credentials not found");
        }
        userCredentialsService.validatePassword(existingUser, password, details.getClientIpAddress());
        //password matched
        SecurityUser securityUser = new SecurityUser(mapper.fromEntity(existingUser));
        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
