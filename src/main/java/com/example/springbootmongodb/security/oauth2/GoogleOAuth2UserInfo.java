package com.example.springbootmongodb.security.oauth2;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getName() {
        return (String)this.attributes.get("name");
    }
    @Override
    public String getEmail() {
        return (String)this.attributes.get("email");
    }
}
