package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResendActivationEmailRequest {
    @Schema(description = "Địa chỉ email đã đăng ký", example = "user0@gmail.com")
    private String email;
}
