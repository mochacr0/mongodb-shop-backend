package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public abstract class AbstractData implements TimestampBased {
    protected String id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
