package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.OrderState;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderStatus {
    private OrderState state;
    private LocalDateTime createdAt;
}
