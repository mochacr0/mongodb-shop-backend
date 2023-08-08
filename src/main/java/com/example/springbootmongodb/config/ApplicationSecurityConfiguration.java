package com.example.springbootmongodb.config;

import com.example.springbootmongodb.security.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfiguration {

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestAuthenticationProvider restAuthenticationProvider;

    @Autowired
    private JwtAccessTokenAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private JwtRefreshTokenAuthenticationProvider jwtRefreshTokenAuthenticationProvider;

    @Autowired
    @Qualifier(value = "RestAuthenticationSuccessHandler")
    private AuthenticationSuccessHandler restAuthenticationSuccessHandler;

    @Autowired
    @Qualifier(value = "RestAuthenticationFailureHandler")
    private AuthenticationFailureHandler restAuthenticationFailureHandler;

    @Autowired
    @Qualifier(value = "OAuth2AuthenticationSuccessHandler")
    private AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Autowired
    @Qualifier(value = "OAuth2AuthenticationFailureHandler")
    private AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenExtractor jwtTokenExtractor;

    @Autowired
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    private static final List<String> NON_TOKEN_BASED_AUTH_ENTRY_ENDPOINTS = new ArrayList<>(Arrays.asList(
            AUTH_LOGIN_ENDPOINT,
            AUTH_ACTIVATE_EMAIL_ROUTE,
            AUTH_RESEND_ACTIVATION_TOKEN_ROUTE,
            AUTH_GET_USER_PASSWORD_POLICY_ROUTE,
            AUTH_REQUEST_PASSWORD_RESET_EMAIL_ROUTE,
            AUTH_RESET_PASSWORD_ROUTE,
            AUTH_REFRESH_TOKEN_ROUTE,
            USERS_REGISTER_USER_ROUTE,
//            "/products/**",
            PRODUCT_GET_PRODUCT_BY_ID_ROUTE,
            PRODUCT_GET_PRODUCTS_ROUTE,
            //momo callback
            ORDER_IPN_REQUEST_CALLBACK_ROUTE,
            "/media/**",
            "/oauth2/**",
            "/favicon.ico",
            "/error/**",
            "/test/**",
            "/categories/**",
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            //TEST
//            USERS_DELETE_USER_BY_ID_ROUTE,
            USERS_ACTIVATE_USER_CREDENTIALS_ROUTE,
            SHIPMENT_GET_LV4_ADDRESSES_ROUTE));

    @Bean
    public AuthenticationManager authenticationManager(ObjectPostProcessor<Object> objectPostProcessor) throws Exception {
        DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
                .postProcess(new DefaultAuthenticationEventPublisher());
        var builder = new AuthenticationManagerBuilder(objectPostProcessor);
        builder.authenticationEventPublisher(eventPublisher);
        builder.authenticationProvider(this.restAuthenticationProvider);
        builder.authenticationProvider(this.jwtAuthenticationProvider);
        builder.authenticationProvider(this.jwtRefreshTokenAuthenticationProvider);
        return builder.build();
    }

    public RestLoginProcessingFilter buildRestLoginProcessingFilter() {
        RestLoginProcessingFilter filter = new RestLoginProcessingFilter(
                AUTH_LOGIN_ENDPOINT,
                restAuthenticationSuccessHandler,
                restAuthenticationFailureHandler,
                mapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    public JwtAuthenticationProcessingFilter buildJwtAuthenticationProcessingFilter() {
        String processingPath = "/**";
        List<String> skipPaths = new ArrayList<>(NON_TOKEN_BASED_AUTH_ENTRY_ENDPOINTS);
        JwtAuthenticationProcessingFilter filter = new JwtAuthenticationProcessingFilter(new SkipPathRequestMatcher(skipPaths, processingPath), restAuthenticationFailureHandler, jwtTokenExtractor);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    public JwtRefreshTokenProcessingFilter buildJwtRefreshTokenProcessingFilter() {
        JwtRefreshTokenProcessingFilter filter = new JwtRefreshTokenProcessingFilter(AUTH_REFRESH_TOKEN_ROUTE, restAuthenticationSuccessHandler, restAuthenticationFailureHandler, mapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    public CorsConfigurationSource buildCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(customizer -> customizer.configurationSource(buildCorsConfigurationSource()))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                    .requestMatchers("/users").permitAll()
//                    .requestMatchers("/oauth2").permitAll()
//                    .requestMatchers("/user").permitAll())
                        .requestMatchers(AUTH_LOGIN_ENDPOINT).permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(buildRestLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtRefreshTokenProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                .authorizationRequestRepository(this.authorizationRequestRepository))
                        .successHandler(this.oauth2AuthenticationSuccessHandler)
                        .failureHandler(this.oauth2AuthenticationFailureHandler));
        return http.build();
    }

}
