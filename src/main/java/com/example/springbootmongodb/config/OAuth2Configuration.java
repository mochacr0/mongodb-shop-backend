package com.example.springbootmongodb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.oauth2")
@Data
public class OAuth2Configuration {
    private String googleClientId;
    private String googleClientSecret;

    @Bean
    public ClientRegistrationRepository buildClientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(registerClientRegistration());
    }

    private List<ClientRegistration> registerClientRegistration() {
        List<ClientRegistration> registrations = new ArrayList();
        registrations.add(buildGoogleClientRegistration());
        return registrations;
    }

    private ClientRegistration buildGoogleClientRegistration() {
        return CommonOAuth2Provider
                .GOOGLE
                .getBuilder("google")
                .clientId(this.googleClientId)
                .clientSecret(this.googleClientSecret)
                .build();
    }
}
