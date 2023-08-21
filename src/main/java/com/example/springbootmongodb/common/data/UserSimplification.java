package com.example.springbootmongodb.common.data;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserSimplification {
    private String id;
    private String name;
}
