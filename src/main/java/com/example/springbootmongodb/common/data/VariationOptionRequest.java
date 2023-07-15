package com.example.springbootmongodb.common.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariationOptionRequest {
    private String id;
    private String name;
    private String imageUrl;
}
