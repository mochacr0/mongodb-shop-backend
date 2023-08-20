package com.example.springbootmongodb.common.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReviewRequest {
    private String productId;
    private double rating;
    private String comment;
    private String processId;
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
}
