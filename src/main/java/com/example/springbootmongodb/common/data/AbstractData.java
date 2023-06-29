package com.example.springbootmongodb.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractData implements TimestampBased {
    protected String id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
