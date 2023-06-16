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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication/Authorization APIs")
public class AuthController {
    private final AuthService authService;
    private final SecuritySettingsConfiguration securitySettings;

    @Operation(summary = "Activate a user account using the activation token retrieved from the email")
    @PostMapping(value = AUTH_ACTIVATE_EMAIL_ROUTE)
    void activateEmail(
            @Parameter(description = "Activate user using activation token retrieved from email")
            @RequestParam String activationToken) {
        authService.activateEmail(activationToken);
    }

    @Operation(summary = "Resend the activation token to the user's email address")
    @PostMapping(value = AUTH_RESEND_ACTIVATION_TOKEN_ROUTE)
    void resendActivationToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                                     description = "Object containing the email")
                               @RequestBody ResendActivationEmailRequest resendRequest, HttpServletRequest request) {
        authService.resendActivationTokenByEmail(resendRequest.getEmail(), request);
    }

    @Operation(summary = "Retrieve the password policy for users")
    @GetMapping(value = AUTH_GET_USER_PASSWORD_POLICY_ROUTE)
    UserPasswordPolicy getUserPasswordPolicy() {
        return securitySettings.getPasswordPolicy();
    }

    @Operation(summary = "Change user's password")
    @PostMapping(value = AUTH_CHANGE_PASSWORD_ROUTE)
    JwtToken changePassword(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                                  description = "Object containing the change password request details")
            @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }

    @Operation(summary = "Request a password reset token to be sent to the user's email address")
    @PostMapping(value = AUTH_REQUEST_PASSWORD_RESET_EMAIL_ROUTE)
    void requestPasswordResetEmail (@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                                          description = "Object containing the email")
                                    @RequestBody PasswordResetEmailRequest passwordResetEmailRequest,
                                    HttpServletRequest request) {
        authService.requestPasswordResetEmail(passwordResetEmailRequest.getEmail(), request);
    }

    @Operation(summary = "Reset user's password using the password reset token")
    @PostMapping(value = AUTH_RESET_PASSWORD_ROUTE)
    void resetPassword (@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                              description = "Object containing the password reset request details")
            @RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
    }
}
