package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {
    @Schema(description = "Current password")
    private String currentPassword;
    @Schema(description = "New password")
    private String newPassword;
    @Schema(description = "Confirm password")
    private String confirmPassword;
}
