package com.example.springbootmongodb.common.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProcessReturnItemsResult {
    private long refundAmount;
    @Builder.Default
    List<ReturnItem> returnItems = new ArrayList<>();
}
