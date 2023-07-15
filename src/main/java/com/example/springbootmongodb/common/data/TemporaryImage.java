package com.example.springbootmongodb.common.data;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TemporaryImage {
    private String url;
    private String processId;
}
