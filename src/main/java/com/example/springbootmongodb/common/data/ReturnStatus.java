package com.example.springbootmongodb.common.data;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReturnStatus {
    private ReturnState state;
    private LocalDateTime createdAt;
}
