package com.example.springbootmongodb.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Schema
public class UserCredentials {
    @Schema(title = "Failed login history", description = "The number of times that user's login has failed")
    private Map<String, FailedLoginAttempt> failedLoginHistory = new HashMap<>();
    @Schema(title = "Hashed password", description = "Raw password hashed using BCryptPasswordEncoder")
    private String hashedPassword;
    @Schema(title = "Activation token", description = "Send this token to activate email")
    private String activationToken;
    @Schema(title = "Activation token expiry time", description = "After this time, the email activation token will be invalid")
    private long activationTokenExpirationMillis;
    @Schema(title = "Reset password token", description = "Send this token to reset password")
    private String passwordResetToken;
    @Schema(title = "Reset password token", description = "After this time, the password reset token will be invalid")
    private long passwordResetTokenExpirationMillis;
    @Schema(title = "Is email verified", description = "A boolean value indicates whether the user's email address has been verified", defaultValue = "false")
    private boolean isVerified;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", rawPassword=");
        builder.append(this.getHashedPassword());
        builder.append(", activateToken=");
        builder.append(this.getActivationToken());
        builder.append(", activationTokenExpirationMillis=");
        builder.append(this.getActivationTokenExpirationMillis());
        builder.append(", passwordResetToken=");
        builder.append(this.getPasswordResetToken());
        builder.append(", passwordResetTokenExpirationMillis");
        builder.append(this.getPasswordResetTokenExpirationMillis());
        builder.append(", isVerified=");
        builder.append(this.isVerified());
        builder.append(", failedLoginHistory=");
        builder.append(this.getFailedLoginHistory());
        return builder.toString();
    }

    public boolean isEnabled(String clientIpAddress) {
        if (this.getFailedLoginHistory().containsKey(clientIpAddress)) {
            return this.getFailedLoginHistory().get(clientIpAddress).isEnabled();
        }
        return true;
    }
}
