package com.example.springbootmongodb.common.data;

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
    private List<String> imageUrls = new ArrayList<>();
    private Review shopResponse;
    @Builder.Default
    private boolean isEdited = false;
    private UserSimplification user;
    @Builder.Default
    private boolean isDisabled = false;
}