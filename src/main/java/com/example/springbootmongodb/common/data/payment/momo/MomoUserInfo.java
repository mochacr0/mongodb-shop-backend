package com.example.springbootmongodb.common.data.payment.momo;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MomoUserInfo {
    private String name;
    private String phoneNumber;
    private String email;
}
