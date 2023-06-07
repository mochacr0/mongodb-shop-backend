package com.example.springbootmongodb.security;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.service.UserCredentialsService;
import com.example.springbootmongodb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class RestAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCredentialsService userCredentialsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication provided");
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        RestAuthenticationDetails details = (RestAuthenticationDetails) authentication.getDetails();
        User existingUser = userService.findByName(username);
        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        //authenticate with password
        if (existingUser.getUserCredentials() == null) {
            throw new UsernameNotFoundException("User credentials not found");
        }
        userCredentialsService.validatePassword(existingUser, password, details.getClientIpAddress());
        //password matched
        SecurityUser securityUser = new SecurityUser(existingUser);
        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
