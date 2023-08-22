package com.example.springbootmongodb.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(value = "isEdited")
    @Builder.Default
    private boolean isEdited = false;
    private UserSimplification user;
    @JsonProperty(value = "isDisabled")
    @Builder.Default
    private boolean isDisabled = false;
}