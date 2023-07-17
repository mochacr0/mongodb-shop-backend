package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserRequest {
    @Schema(description = "Username", example = "user00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "Email", example = "nthai2001cr@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema( description = "Mật khẩu", example = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "Nhập lại mật khẩu", example = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
    @Schema(hidden = true)
    public void setMatchedPasswords(String password) {
        this.password = password;
        this.confirmPassword = password;
    }
}
