package com.example.springbootmongodb.security.oauth2;

import java.util.Map;

public class OAuth2UserInfoMapper {
    public static OAuth2UserInfo getOAuth2UserInfo(Map<String, Object> attributes, String registrationId) {
        OAuth2UserInfo oAuth2UserInfo = null;
        switch(registrationId) {
            case "google":
                oAuth2UserInfo = new GoogleOAuth2UserInfo(attributes);
                break;
            default:
        }
        return oAuth2UserInfo;
    }
}
