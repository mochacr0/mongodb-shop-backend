package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
