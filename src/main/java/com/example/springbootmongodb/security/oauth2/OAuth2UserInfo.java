package com.example.springbootmongodb.security.oauth2;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    protected Map<String, Object> attributes;
    public abstract String getName();
    public abstract String getEmail();
}
