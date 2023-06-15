package com.example.springbootmongodb.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginRequest {
    @Schema(title = "username", example = "user00")
    private String username;
    @Schema(title = "password", example = "String")
    private String password;
}
