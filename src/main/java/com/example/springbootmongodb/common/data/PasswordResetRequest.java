package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Schema(description = "Password reset token")
    private String passwordResetToken;
    @Schema(description = "New password")
    private String newPassword;
    @Schema(description = "Confirm password")
    private String confirmPassword;
}
