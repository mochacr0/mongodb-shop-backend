package com.example.springbootmongodb.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariationOptionRequest {
    private String id;
    private String name;
}
