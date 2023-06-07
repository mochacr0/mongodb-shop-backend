package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.ChangePasswordRequest;
import com.example.springbootmongodb.common.data.PasswordResetEmailRequest;
import com.example.springbootmongodb.common.data.PasswordResetRequest;
import com.example.springbootmongodb.common.data.ResendActivationEmailRequest;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.config.UserPasswordPolicy;
import com.example.springbootmongodb.security.JwtToken;
import com.example.springbootmongodb.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@Tag(name = "Auth", description = "Authentication/Authorization APIs")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private SecuritySettingsConfiguration securitySettings;

    @Operation(tags = {"Auth"}, summary = "Activate user")
    @PostMapping(value = AUTH_ACTIVATE_EMAIL_ROUTE)
    void activateEmail(
            @Parameter(description = "Activation token retrieved from email")
            @RequestParam String activationToken) {
        authService.activateEmail(activationToken);
    }

    @Operation(tags = {"Auth"}, summary = "Resend activation token")
    @PostMapping(value = AUTH_RESEND_ACTIVATION_TOKEN_ROUTE)
    void resendActivationToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                               @RequestBody ResendActivationEmailRequest resendRequest, HttpServletRequest request) {
        authService.resendActivationTokenByEmail(resendRequest.getEmail(), request);
    }

    @Operation(tags = {"Auth"}, summary = "Get user password policy")
    @GetMapping(value = AUTH_GET_USER_PASSWORD_POLICY_ROUTE)
    UserPasswordPolicy getUserPasswordPolicy() {
        return securitySettings.getPasswordPolicy();
    }

    @Operation(tags = {"Auth"}, summary = "Request change password token")
    @PostMapping(value = AUTH_CHANGE_PASSWORD_ROUTE)
    JwtToken changePassword(@RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }

    @Operation(tags = {"Auth"}, summary = "Request password reset token")
    @PostMapping(value = AUTH_REQUEST_PASSWORD_RESET_EMAIL_ROUTE)
    void requestPasswordResetEmail (@RequestBody PasswordResetEmailRequest passwordResetEmailRequest,
                                    HttpServletRequest request) {
        authService.requestPasswordResetEmail(passwordResetEmailRequest.getEmail(), request);
    }

    @Operation(tags = {"Auth"}, summary = "Reset password by password reset token")
    @PostMapping(value = AUTH_RESET_PASSWORD_ROUTE)
    void resetPassword (@RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
    }
}
