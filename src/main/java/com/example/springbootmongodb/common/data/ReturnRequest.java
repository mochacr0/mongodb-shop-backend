package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReturnRequest {
    @Required(fieldName = "Order Id")
    private String orderId;
    @Required(fieldName = "Return reason")
    private String reason;
    private String description;
    @Required(fieldName = "Return offer")
    private String offer;
    private long refundAmount;
    private List<ReturnItemRequest> items = new ArrayList<>();
}
