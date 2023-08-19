package com.example.springbootmongodb.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ReviewEntity extends AbstractEntity {
    private double rating;
    private String comment;
    @Builder.Default
    private List<String> images = new ArrayList<>();
    private ReviewEntity shopResponse;
}
