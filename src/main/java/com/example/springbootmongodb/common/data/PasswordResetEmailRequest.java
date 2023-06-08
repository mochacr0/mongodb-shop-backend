package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordResetEmailRequest {
    @Schema(description = "Email address of the user who wants to reset their password", example = "user0@gmail.com")
    private String email;
}
