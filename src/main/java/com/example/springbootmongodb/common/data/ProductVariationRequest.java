package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.ToEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class ProductVariationRequest {
    private String id;
    private String productId;
    private String name;
    List<VariationOptionRequest> options = new ArrayList<>();
}
