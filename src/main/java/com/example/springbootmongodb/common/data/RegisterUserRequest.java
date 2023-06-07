package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserRequest {
    @Schema(title = "name", description = "User name", example = "user00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(title = "email", description = "User email", example = "nthai2001cr@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(title = "password", description = "User password", example = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(title = "confirmPassword", description = "Confirm password, which must be the same as password", example = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
    @Schema(hidden = true)
    public void setMatchedPasswords(String password) {
        this.password = password;
        this.confirmPassword = password;
    }
}
