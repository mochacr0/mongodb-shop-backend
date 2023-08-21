package com.example.springbootmongodb.common.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TemporaryImages {
    private String processId;
    @Builder.Default
    private List<String> urls = new ArrayList<>();
}
