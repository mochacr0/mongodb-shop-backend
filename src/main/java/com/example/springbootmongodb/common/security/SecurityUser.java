package com.example.springbootmongodb.common.security;

import com.example.springbootmongodb.common.data.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityUser extends User {
    Collection<GrantedAuthority> authorities;

    public SecurityUser(){}
    public SecurityUser(User user) {
        super(user);
    }

    public Collection<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            return Stream.of(this.getAuthority())
                    .map(authority -> new SimpleGrantedAuthority(authority != null ? authority.name() : "NULL"))
                    .collect(Collectors.toList());
        }
        return authorities;
    }
}
