package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {
    @Schema(description = "Mật khẩu hiện tại")
    private String currentPassword;
    @Schema(description = "Mật khẩu mới")
    private String newPassword;
    @Schema(description = "Nhập lại mật khẩu mới")
    private String confirmPassword;
}
