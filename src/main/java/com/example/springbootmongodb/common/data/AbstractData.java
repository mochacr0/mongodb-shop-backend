package com.example.springbootmongodb.common.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
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
