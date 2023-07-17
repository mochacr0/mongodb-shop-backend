package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordResetEmailRequest {
    @Schema(description = "Địa chỉ email của tài khoản yêu cầu reset mật khẩu", example = "user0@gmail.com")
    private String email;
}
