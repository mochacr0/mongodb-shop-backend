package com.example.springbootmongodb.common.data;

import lombok.Data;

@Data
public class PasswordResetEmailRequest {
    private String email;
}
