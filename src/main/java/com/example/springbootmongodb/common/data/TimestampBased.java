package com.example.springbootmongodb.common.data;

import java.time.LocalDateTime;

public interface TimestampBased {
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
