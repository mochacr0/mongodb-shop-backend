package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReturnRequest {
    @Schema(description = "Order Id")
    @Required(fieldName = "Order Id")
    private String orderId;
    @Schema(description = "Lý do trả hàng/hoàn tiền (missing items, damaged items, wrong items)", example = "missing items")
    @Required(fieldName = "Return reason")
    private String reason;
    @Schema(description = "Mô tả chi tiết", example = "Tay áo bị rách")
    private String description;
    @Schema(description = "Để xuất cách giải quyết (return refund, refund)", example = "return refund")
    @Required(fieldName = "Return offer")
    private String offer;
    @Schema(description = "Số tiền hoàn trả", example = "10000")
    @Required(fieldName = "Return offer")
    private long refundAmount;
    private List<ReturnItemRequest> items = new ArrayList<>();
}
