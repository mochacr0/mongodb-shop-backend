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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "Kích hoạt tài khoản user")
    @PostMapping(value = AUTH_ACTIVATE_EMAIL_ROUTE)
    void activateEmail(
            @Parameter(description = "Token kích hoạt được gửi đến email đã đăng ký")
            @RequestParam String activationToken) {
        authService.activateEmail(activationToken);
    }

    @Operation(summary = "Yêu cầu gửi lại token kích hoạt đến email đã đăng ký")
    @PostMapping(value = AUTH_RESEND_ACTIVATION_TOKEN_ROUTE)
    void resendActivationToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                               @RequestBody ResendActivationEmailRequest resendRequest, HttpServletRequest request) {
        authService.resendActivationTokenByEmail(resendRequest.getEmail(), request);
    }

    @Operation(summary = "Truy xuất yêu cầu đối với mật khẩu (Số lượng ký tự thường, ký tự viết hoa,...)")
    @GetMapping(value = AUTH_GET_USER_PASSWORD_POLICY_ROUTE)
    UserPasswordPolicy getUserPasswordPolicy() {
        return securitySettings.getPasswordPolicy();
    }

    @Operation(summary = "Đổi mật khẩu",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    @PostMapping(value = AUTH_CHANGE_PASSWORD_ROUTE)
    JwtToken changePassword(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }

        @Operation(summary = "Yêu cầu gửi token reset mật khẩu đến địa chỉ email của người dùng")
    @PostMapping(value = AUTH_REQUEST_PASSWORD_RESET_EMAIL_ROUTE)
    void requestPasswordResetEmail (@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                    @RequestBody PasswordResetEmailRequest passwordResetEmailRequest,
                                    HttpServletRequest request) {
        authService.requestPasswordResetEmail(passwordResetEmailRequest.getEmail(), request);
    }

    @Operation(summary = "Reset mật khẩu bằng token reset đã gửi đến email")
    @PostMapping(value = AUTH_RESET_PASSWORD_ROUTE)
    void resetPassword (@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
    }
}
