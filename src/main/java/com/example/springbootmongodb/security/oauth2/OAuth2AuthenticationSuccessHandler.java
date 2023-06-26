package com.example.springbootmongodb.security.oauth2;

import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.mapper.UserMapper;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.config.UserPasswordPolicy;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.security.JwtToken;
import com.example.springbootmongodb.security.JwtTokenFactory;
import com.example.springbootmongodb.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.example.springbootmongodb.controller.ControllerConstants.OAUTH2_AUTHENTICATION_SUCCESS_REDIRECT;

@Slf4j
@Component(value = "OAuth2AuthenticationSuccessHandler")
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenFactory jwtTokenFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private SecuritySettingsConfiguration securitySettings;
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    @Autowired
    private UserMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken)authentication;
            OAuth2User oauth2User = authenticationToken.getPrincipal();
            SecurityUser securityUser = getOrCreateSecurityUserFromOAuth2User(oauth2User, authenticationToken.getAuthorizedClientRegistrationId(), request);
            JwtToken jwtAccessToken = this.jwtTokenFactory.createAccessToken(securityUser);
            this.getRedirectStrategy().sendRedirect(request, response,  OAUTH2_AUTHENTICATION_SUCCESS_REDIRECT + jwtAccessToken.getValue());
        }
        catch(Exception e) {
            log.debug("Error occurred during processing authentication success result. " +
                    "request [{}], response [{}], authentication [{}]", request, response, authentication, e);
            clearAuthenticationAttributes(request, response);
            String errorPath = "/oauth2?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            this.getRedirectStrategy().sendRedirect(request, response, errorPath);
        }
    }

    private SecurityUser getOrCreateSecurityUserFromOAuth2User(OAuth2User oauth2User, String registrationId, HttpServletRequest request) {
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoMapper.getOAuth2UserInfo(oauth2User.getAttributes(), registrationId);
        UserEntity user;
        try {
            user = userService.findByEmail(oauth2UserInfo.getEmail());
        }
        catch (ItemNotFoundException ex) {
            RegisterUserRequest registerUserRequest = new RegisterUserRequest();
            PasswordGenerator passwordGenerator = new PasswordGenerator();
            UserPasswordPolicy passwordPolicy = securitySettings.getPasswordPolicy();
            String rawPassword = passwordGenerator.generatePassword(passwordPolicy.getMinimumLength(), passwordPolicy.getPasswordCharacterRules());
            registerUserRequest.setName(oauth2UserInfo.getName());
            registerUserRequest.setEmail(oauth2UserInfo.getEmail());
            registerUserRequest.setMatchedPasswords(rawPassword);
            user = userService.register(registerUserRequest, request, false);
        }
        return new SecurityUser(mapper.toUser(user));
    }
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

}
