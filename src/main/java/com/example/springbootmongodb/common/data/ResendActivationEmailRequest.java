package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResendActivationEmailRequest {
    private String email;
}
