package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ReviewEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Review extends AbstractData {
    private double rating;
    private String comment;
    @Builder.Default
    private List<String> images = new ArrayList<>();
    private Review shopResponse;
}
