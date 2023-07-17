package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Schema(description = "Token reset mật khẩu")
    private String passwordResetToken;
    @Schema(description = "Mật khẩu mới")
    private String newPassword;
    @Schema(description = "Nhập lại mật khẩu mới")
    private String confirmPassword;
}
