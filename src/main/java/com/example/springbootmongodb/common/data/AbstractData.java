package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Id", example = "647d222a59a4582894a95c10")
    protected String id;
    @Schema(description = "Thời điểm được tạo")
    protected LocalDateTime createdAt;
    @Schema(description = "Lần cập nhập gần nhất")
    protected LocalDateTime updatedAt;
}
