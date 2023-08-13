package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.AbstractItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class OrderItem extends AbstractItem {
    @JsonProperty(value = "isRefundRequested")
    private boolean isRefundRequested;
}
